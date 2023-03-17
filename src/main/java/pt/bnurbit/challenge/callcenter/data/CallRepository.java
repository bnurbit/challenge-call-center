package pt.bnurbit.challenge.callcenter.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallRepository extends JpaRepository<CallRecord, Long> {

    Page<CallRecord> findAllByType(CallType type, Pageable pageable);
}
