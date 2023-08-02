package ru.practicum.shareit.booking.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT it FROM Item AS it WHERE it.owner.id = ?1")
    List<Item> findAllItemByUser(long idUser, PageRequest pageRequest);

    @Query("SELECT it FROM Item AS it WHERE (LOWER(it.name) LIKE CONCAT ('%',LOWER(?1),'%') " +
            "OR LOWER(it.description) LIKE CONCAT ('%',LOWER(?1),'%')) AND it.available=true")
    List<Item> searchItem(String text, PageRequest pageRequest);

    List<Item> findAllByRequestId(long requestId);

}
