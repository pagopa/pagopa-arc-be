package it.gov.pagopa.arc.dto.mapper;

import it.gov.pagopa.arc.connector.bizevents.dto.BizEventsWalletInfoDTO;
import it.gov.pagopa.arc.model.generated.WalletInfoDTO;
import org.springframework.stereotype.Service;

@Service
public class BizEventsWalletInfo2WalletInfoDTO {

    public WalletInfoDTO mapWalletInfo(BizEventsWalletInfoDTO bizEventsWalletInfoDTO){
        return WalletInfoDTO.builder()
                .accountHolder(bizEventsWalletInfoDTO.getAccountHolder())
                .brand(bizEventsWalletInfoDTO.getBrand())
                .blurredNumber(bizEventsWalletInfoDTO.getBlurredNumber())
                .maskedEmail(bizEventsWalletInfoDTO.getMaskedEmail())
                .build();
    }
}