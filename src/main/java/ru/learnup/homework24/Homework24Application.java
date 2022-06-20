package ru.learnup.homework24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.learnup.homework24.db.dao.AuthorDao;
import ru.learnup.homework24.db.dao.BookDao;
import ru.learnup.homework24.db.dao.BuyDao;
import ru.learnup.homework24.db.dao.StockDao;
import ru.learnup.homework24.db.entity.*;
import ru.learnup.homework24.db.exeption.InvalidQuantityException;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Homework24Application {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(Homework24Application.class, args);
		AuthorDao authorDao = context.getBean(AuthorDao.class);
		StockDao stockDao = context.getBean(StockDao.class);
		BookDao bookDao = context.getBean(BookDao.class);
		BuyDao buyDao = context.getBean(BuyDao.class);
		Author author = new Author("Mark", "Tven");
		authorDao.save(author);
		Book book = new Book(1999, "avvcd", 101, 76, author);
		Stock stock = new Stock(book, 1);
		stockDao.save(stock);
		Book book1 = bookDao.findById(1);
		Buy buy = new Buy();
		buyDao.save(buy);

		ExecutorService es = Executors.newFixedThreadPool(2);

		for (int i = 0; i < 2; i++) {
			int finalI = i;
			es.execute(() -> {
				System.out.println("start thread " + finalI);
				buyBook(book1, stockDao, buy, finalI);
			});
		}
		es.shutdown();

	}

	public static void buyBook(Book book, StockDao stockDao, Buy buy, int numberThread) throws InvalidQuantityException {

		try {

			Stock stock = stockDao.findById(book.getId());
			if (buy.getBuyDetails() == null) {
				BuyDetails buyDetails = new BuyDetails();
				buy.setBuyDetails(buyDetails);
				buy.getBuyDetails().setBooks(new ArrayList<>());
			}
			stockDao.update(stock);
			buy.getBuyDetails().addBook(book);
			System.out.println("Товар добавлен. " + "Thread " + numberThread);
		} catch (ObjectOptimisticLockingFailureException e) {
			System.out.println("Данный товар уже куплен другим пользователем. " + "Thread " + numberThread);
		}
	}
}
