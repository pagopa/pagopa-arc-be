package it.gov.pagopa.arc.config;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.arc.controller.generated.ArcAuthApi;
import it.gov.pagopa.arc.controller.generated.ArcZendeskAssistanceApi;
import it.gov.pagopa.arc.security.CustomLogoutHandler;
import it.gov.pagopa.arc.security.CustomLogoutSuccessHandler;
import it.gov.pagopa.arc.service.AuthService;
import it.gov.pagopa.arc.service.CustomAuthenticationSuccessHandler;
import it.gov.pagopa.arc.service.TokenStoreService;
import it.gov.pagopa.arc.service.ZendeskAssistanceTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;


@WebMvcTest({ArcAuthApi.class, ArcZendeskAssistanceApi.class})
@Import(OAuth2LoginConfig.class)
class OAuth2LoginConfigTest {

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepositoryMock;
    @MockBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandlerMock;

    @MockBean
    private CustomLogoutHandler customLogoutHandler;

    @MockBean
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @MockBean
    private ZendeskAssistanceTokenService zendeskAssistanceTokenServiceMock;
    @MockBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private TokenStoreService tokenStoreService;

    @Test
    void givenURLWithoutCodeAndStateWhenWithoutAccessTokenThenRedirectToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/token/oneidentity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void givenURLWhenWithoutAccessTokenThenRedirectToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/token/oneidentity?code=fakeCode&state=fakeState"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void givenSecurityURLWhenCallEndpointThenNoRedirect() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/token/assistance")
                        .param("userEmail", "someone@email.com"))
                .andExpect(status().is4xxClientError()).andReturn();
        Assertions.assertNotEquals(302,result.getResponse().getStatus());
    }

}