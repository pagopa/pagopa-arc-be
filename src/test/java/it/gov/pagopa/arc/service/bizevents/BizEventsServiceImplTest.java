package it.gov.pagopa.arc.service.bizevents;

import it.gov.pagopa.arc.connector.bizevents.BizEventsConnector;
import it.gov.pagopa.arc.connector.bizevents.dto.BizEventsTransactionDTO;
import it.gov.pagopa.arc.connector.bizevents.dto.BizEventsTransactionsListDTO;
import it.gov.pagopa.arc.dto.mapper.BizEventsTransactionDTO2TransactionDTO;
import it.gov.pagopa.arc.dto.mapper.BizEventsTransactionsListDTO2TransactionsListDTO;
import it.gov.pagopa.arc.fakers.BizEventsTransactionDTOFaker;
import it.gov.pagopa.arc.fakers.TransactionDTOFaker;
import it.gov.pagopa.arc.model.generated.TransactionDTO;
import it.gov.pagopa.arc.model.generated.TransactionsListDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
@ExtendWith(MockitoExtension.class)
class BizEventsServiceImplTest {
    private static final int PAGE = 1;
    private static final int SIZE = 2;
    private static final String FILTER = "DUMMY_FILTER";

    private static final String DUMMY_FISCAL_CODE = "DUMMY_FISCAL_CODE";

    @Autowired
    private BizEventsService bizEventsService;

    @Mock
    private BizEventsConnector bizEventsConnectorMock;
    @Mock
    private BizEventsTransactionDTO2TransactionDTO transactionDTOMapperMock;
    @Mock
    private BizEventsTransactionsListDTO2TransactionsListDTO transactionsListDTOMapperMock;

    @BeforeEach
    void setUp() {
        bizEventsService = new BizEventsServiceImpl(DUMMY_FISCAL_CODE,bizEventsConnectorMock , transactionDTOMapperMock, transactionsListDTOMapperMock);
    }

    @Test
    void givenPageSizeFilterWhenCallRetrieveTransactionsListFromBizEventsThenReturnTransactionList() {
        //given
        BizEventsTransactionDTO bizEventsTransactionDTO = BizEventsTransactionDTOFaker.mockInstance(1, false);
        BizEventsTransactionDTO bizEventsTransactionDTO2 = BizEventsTransactionDTOFaker.mockInstance(2, true);

        TransactionDTO transactionDTO = TransactionDTOFaker.mockInstance(1, false);
        TransactionDTO transactionDTO2 = TransactionDTOFaker.mockInstance(2, true);

        List<BizEventsTransactionDTO> bizEventsTransactionDTOList = List.of(
                bizEventsTransactionDTO,
                bizEventsTransactionDTO2
        );

        List<TransactionDTO> transactions = List.of(
                transactionDTO,
                transactionDTO2
        );

        BizEventsTransactionsListDTO bizEventsTransactionsListDTO = BizEventsTransactionsListDTO
                .builder()
                .transactions(bizEventsTransactionDTOList)
                .build();

        TransactionsListDTO expectedResult = TransactionsListDTO
                .builder()
                .transactions(transactions)
                .currentPage(1)
                .totalPages(1)
                .totalItems(10)
                .itemsForPage(SIZE)
                .build();

        Mockito.when(bizEventsConnectorMock.getTransactionsList(DUMMY_FISCAL_CODE,SIZE)).thenReturn(bizEventsTransactionsListDTO);
        Mockito.when(transactionDTOMapperMock.apply(bizEventsTransactionDTO)).thenReturn(transactionDTO);
        Mockito.when(transactionDTOMapperMock.apply(bizEventsTransactionDTO2)).thenReturn(transactionDTO2);
        Mockito.when(transactionsListDTOMapperMock.apply(transactions,SIZE)).thenReturn(expectedResult);

        //when
        TransactionsListDTO result = bizEventsService.retrieveTransactionsListFromBizEvents(PAGE, SIZE, FILTER);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTransactions().size());
        Assertions.assertEquals(transactions,expectedResult.getTransactions());
        Assertions.assertEquals(1, result.getCurrentPage());
        Assertions.assertEquals(2, result.getItemsForPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getTotalItems());
        Mockito.verify(bizEventsConnectorMock).getTransactionsList(anyString(),anyInt());
        Mockito.verify(transactionDTOMapperMock,Mockito.times(2)).apply(any());
        Mockito.verify(transactionsListDTOMapperMock).apply(any(),anyInt());

    }

    @Test
    void givenPageSizeFilterWhenCallRetrieveTransactionsListThenReturnEmptyTransactionList() {
        //given
        List<TransactionDTO> transactions = new ArrayList<>();

        BizEventsTransactionsListDTO bizEventsTransactionsListDTO = BizEventsTransactionsListDTO
                .builder()
                .transactions(new ArrayList<>())
                .build();

        TransactionsListDTO expectedResult = TransactionsListDTO
                .builder()
                .transactions(transactions)
                .currentPage(1)
                .totalPages(1)
                .totalItems(10)
                .itemsForPage(SIZE)
                .build();

        Mockito.when(bizEventsConnectorMock.getTransactionsList(DUMMY_FISCAL_CODE,SIZE)).thenReturn(bizEventsTransactionsListDTO);
        Mockito.when(transactionsListDTOMapperMock.apply(transactions,SIZE)).thenReturn(expectedResult);
        //when
        TransactionsListDTO result = bizEventsService.retrieveTransactionsListFromBizEvents(PAGE, SIZE, FILTER);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getTransactions().isEmpty());
        Assertions.assertEquals(1, result.getCurrentPage());
        Assertions.assertEquals(2, result.getItemsForPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getTotalItems());
        Mockito.verify(bizEventsConnectorMock).getTransactionsList(anyString(),anyInt());
        Mockito.verifyNoInteractions(transactionDTOMapperMock);
        Mockito.verify(transactionsListDTOMapperMock).apply(any(),anyInt());

    }
}