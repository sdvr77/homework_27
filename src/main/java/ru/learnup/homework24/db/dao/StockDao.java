package ru.learnup.homework24.db.dao;

import org.springframework.stereotype.Component;
import ru.learnup.homework24.db.entity.Stock;
import ru.learnup.homework24.db.repository.StockRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class StockDao {

    private final StockRepository repository;
    @PersistenceContext
    private final EntityManager entityManager;

    public StockDao(StockRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    public Stock findById(int id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Stock> findAll() {
        List<Stock> stockList = new ArrayList<>();
        Iterable<Stock> stock = repository.findAll();
        stock.forEach(stockList::add);
        return stockList;
    }

    public void save(Stock stock) {
        repository.save(stock);
    }

    @Transactional
    public void update(Stock stock) {
        Stock updateStock = repository.findById(stock.getId()).orElseThrow(EntityNotFoundException::new);
        entityManager.lock(updateStock, LockModeType.OPTIMISTIC);
        System.out.println("Транзакция заблокирована, id " + stock.getId());
        int amount = updateStock.getAmount();
        updateStock.setAmount(amount - 1);
        repository.save(updateStock);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
