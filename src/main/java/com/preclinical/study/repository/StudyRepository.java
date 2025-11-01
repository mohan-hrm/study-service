package com.preclinical.study.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.preclinical.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {
	
	@EntityGraph(attributePaths = "phases")
    Optional<Study> findByStudyCode(String studyCode);
    boolean existsByProtocolNumber(String protocolNumber);
 // ✅ Find studies by sponsor
    List<Study> findBySponsorNameIgnoreCase(String sponsorName);

    // ✅ Find ongoing studies only
    @EntityGraph(attributePaths = "phases")
    @Query("SELECT s FROM Study s WHERE s.status = 'Ongoing'")
    List<Study> findOngoingStudies();

    // ✅ Find by dynamic date range (JPQL)
    @EntityGraph(attributePaths = "phases")
    @Query("SELECT s FROM Study s WHERE s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Study> findStudiesWithinDates(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // ✅ Paginated search by status
    @EntityGraph(attributePaths = "phases")
    Page<Study> findByStatus(String status, Pageable pageable);

    // ✅ Update study status (without fetching)
    @Modifying
    @Query("UPDATE Study s SET s.status = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.studyId = :id")
    int updateStudyStatus(@Param("id") Long id, @Param("status") String status);
    
    @EntityGraph(attributePaths = "phases")
    List<Study> findAll(); // ✅ applies to default findAll()
}
