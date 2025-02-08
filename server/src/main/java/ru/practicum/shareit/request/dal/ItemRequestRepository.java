package ru.practicum.shareit.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorOrderByCreatedDesc(Long userId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(@Param("userId") long userId);
}
