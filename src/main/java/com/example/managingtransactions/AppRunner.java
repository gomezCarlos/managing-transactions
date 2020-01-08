package com.example.managingtransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AppRunner implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

	private final BookingService bookingService;

	public AppRunner(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@Override
	public void run(String... args) throws Exception {

		bookingService.book("Alice", "Bob", "Carol");
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "First booking should work with no problem");
		LOGGER.info("Alice, Bob, and Carol have been booked");
		try {
			bookingService.book("Chris", "Samuel");
		} catch (RuntimeException e) {
			LOGGER.info("v--- The following exception is expect because 'Samuel is too big for the DB ---v'");
			LOGGER.error(e.getMessage());
		}

		for (String person : bookingService.findAllBookings()) {
			LOGGER.info("So far, {} is booked", person);
		}
		LOGGER.info(
				"You shouldn't see Chris or Samuel. Samuel violated DB constraints, and Chris was Rolled back in the same TX");
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "'Samuel' should have triggered a rollback");

		try {
			bookingService.book("Buddy", null);
		} catch (RuntimeException e) {
			LOGGER.info("v--- The following exception is expect because null is not " + "valid for the DB ---v");
			LOGGER.error(e.getMessage());
		}
		for (String person : bookingService.findAllBookings()) {
			LOGGER.info("So far, " + person + " is booked.");
		}
		LOGGER.info("You shouldn't see Buddy or null. null violated DB constraints, and "
				+ "Buddy was rolled back in the same TX");
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "'null' should have triggered a rollback");

	}
}
