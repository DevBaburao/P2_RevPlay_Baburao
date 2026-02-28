package com.rev.app.repository;

import com.rev.app.entity.ListeningHistory;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findTop50ByUserOrderByPlayedAtDesc(User user);
}
