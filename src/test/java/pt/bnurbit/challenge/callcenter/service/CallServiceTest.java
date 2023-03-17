package pt.bnurbit.challenge.callcenter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.bnurbit.challenge.callcenter.data.CallRecord;
import pt.bnurbit.challenge.callcenter.data.CallRepository;
import pt.bnurbit.challenge.callcenter.dto.CallInputDTO;
import pt.bnurbit.challenge.callcenter.exceptions.CallRecordNotFoundException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class CallServiceTest {

    @Autowired
    private CallService service;

    @MockBean
    private CallRepository repository;

    @Test
    void testCreateSingle() {
        // Arrange
        CallInputDTO callInputDTO = new CallInputDTO("1", "2", "Inbound", Instant.now(), Instant.now());
        CallRecord callRecord = new CallRecord(callInputDTO);
        doReturn(callRecord).when(repository).save(any());

        // Act
        CallRecord returnedCallRecord = service.create(callInputDTO);

        // Assert
        assertNotNull(returnedCallRecord, "The saved record should not be null");
    }

    @Test
    void testCreateMany() {
        // Arrange
        List<CallInputDTO> callInputDTOS = List.of(
            new CallInputDTO("1", "2", "Inbound", Instant.now(), Instant.now()),
            new CallInputDTO("2", "3", "Inbound", Instant.now(), Instant.now()),
            new CallInputDTO("3", "4", "Outbound", Instant.now(), Instant.now())
        );

        List<CallRecord> callRecords = CallService.fromCallInputList(callInputDTOS);
        doReturn(callRecords).when(repository).saveAll(any());

        // Act
        List<CallRecord> returnedCallRecords = service.create(callInputDTOS);

        // Assert
        assertNotNull(returnedCallRecords, "The saved record should not be null");
        assertEquals(3, returnedCallRecords.size(), "The list should have 3 records");
    }

    @Test
    void testDeleteNotFound() {

        // Arrange
        doReturn(false).when(repository).existsById(any());
        // Assert
        assertThrows(CallRecordNotFoundException.class, () -> service.delete(anyLong()));
    }

    @Test
    void testDeleteSuccess() {

        // Arrange
        doReturn(true).when(repository).existsById(any());

        // Act
        assertDoesNotThrow(() -> service.delete(anyLong()));
    }
}