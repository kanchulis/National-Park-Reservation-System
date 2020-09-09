package com.techelevator.tenmo;

import java.util.Scanner;
import com.techelevator.tenmo.models.Accounts;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };

	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	UserService userService = new UserService(API_BASE_URL);
	Accounts account = new Accounts();

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				pendingRequests();
			} else {

				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		int userId = currentUser.getUser().getId();
		Double currentBalance = userService.viewBalance(userId);
		System.out.println("Your current account balance is: $" + currentBalance + "0");

	}

	private void viewTransferHistory() {

		int userId = currentUser.getUser().getId();
		String username = (currentUser.getUser().getUsername());

		int accountNumber = userService.getAccount(currentUser.getToken(), userId).getAccountId();

		System.out.println("-------------------------------------");
		System.out.println(String.format("%1$-15s", "Transfer ID") + String.format("%1$-15s", "From:")
				+ String.format("%1$-10s", "Amount"));
		System.out.println("-------------------------------------");

		Transfer[] transfer = userService.getTransferHisotry(currentUser.getToken(), accountNumber);

		for (Transfer aTransfer : transfer) {

			System.out.println(String.format("%1$-15s", aTransfer.getTransferId()) + String.format("%1$-15s", username)
					+ String.format("%1$-10s", aTransfer.getAmount()) + "\n");
		}

		System.out.println("-------------------------------------");
		int transferId = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel): ");

		viewTransferDetails(transferId); // Goes to method to view transfer details

	}

	private void viewTransferDetails(int transferId) {
		String userToken = currentUser.getToken();

		System.out.println("-------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("-------------------------------------");

		Transfer[] info = userService.getTransferById(userToken, transferId);
		for (Transfer placeholder : info) {

			System.out.println("Id: " + placeholder.getTransferId());
			System.out.println("From: " + currentUser.getUser().getUsername());
			System.out.println("To (ID): " + placeholder.getAccountTo());
			System.out.println("Type: " + placeholder.getTransferTypeId());
			System.out.println("Status: " + placeholder.getTransferStatusId());
			System.out.println("Amount: " + placeholder.getAmount());

		}

	}

	private void viewPendingRequests() {
		int userID = currentUser.getUser().getId();
		int accountNumber = userService.getAccount(currentUser.getToken(), userID).getAccountId();
		Transfer[] pendingRequests = userService.getPendingTransfers(currentUser.getToken(), accountNumber);

		String[] otherParty = new String[pendingRequests.length];
		for (int i = 0; i < pendingRequests.length; i++) {
			otherParty[i] = userService.getUserWithAccount(currentUser.getToken(), pendingRequests[i].getAccountTo())
					.getUsername();
		}
		console.viewPendingRequests(pendingRequests, otherParty);

		approveOrReject(userID);
	}

	private void approveOrReject(int userIDFrom) {
		int transactionToApproveReject = console.approveRejectPrompt();
		if (transactionToApproveReject == 0) {
			return;
		}
		Transfer theTransfer = userService.getTransferByIdSingle(currentUser.getToken(), transactionToApproveReject);
		int userIDTo = userService.getUserWithAccount(currentUser.getToken(), theTransfer.getAccountTo()).getId();
		double amount = theTransfer.getAmount();

		int approvalChoice = console.DisplayApproveRejectScreen();
		if (approvalChoice == 0) {
			return;
		}

		if (approvalChoice == 1) {

			if (amount > userService.getAccount(currentUser.getToken(), userIDFrom).getBalance()) {
				System.out.println("Insufficient funds.");
				return;
			}

			theTransfer.setTransferStatusId(2);
			userService.updateTransferStatus(currentUser.getToken(), theTransfer);
			userService.withdraw(currentUser.getToken(), userIDFrom, amount);
			userService.deposit(currentUser.getToken(), userIDTo, amount);
		}

		if (approvalChoice == 2) {
			theTransfer.setTransferStatusId(3);
			userService.updateTransferStatus(currentUser.getToken(), theTransfer);
		}

	}

	private void sendBucks() {

		String userToken = currentUser.getToken();

		// Display list of users in the system with their ID

		int userId = currentUser.getUser().getId();
		Scanner amountDouble = new Scanner(System.in);
		System.out.println("Enter amount: $");
		Double amount = amountDouble.nextDouble();

		System.out.println("-------------------------------------");
		System.out.println("Enter ID of user you are sending to:");
		System.out.println("-------------------------------------");

		String moneyToInput = console.getChoiceFromOptions(userService.findAll(userToken)).toString();
		int moneyTo = console.getUserInputInteger("Confirm the user again:" + "\n");

		if (amount > userService.getAccount(currentUser.getToken(), userId).getBalance()) {
			System.out.println("Not enough funds");
			return;
		}

		userService.sendTransfer(userToken, userId, moneyTo, amount);
		System.out.println("Your transfer was sucessful");

		System.out.println("-------------------------------------");

	}

	private void requestBucks() {

		String userToken = currentUser.getToken();
		int userId = currentUser.getUser().getId();

		Scanner amountDouble = new Scanner(System.in);

		System.out.println("Enter amount: $");

		Double amount = amountDouble.nextDouble();

		System.out.println("-------------------------------------");
		System.out.println("Enter ID of user you are requesting from:");
		System.out.println("-------------------------------------");

		String moneyFromInput = console.getChoiceFromOptions(userService.findAll(userToken)).toString();
		int moneyFrom = console.getUserInputInteger("Confirm the user again:" + "\n");

		userService.requestTransfer(userToken, moneyFrom, userId, amount); // Populates the transfers table
		System.out.println("Your transfer is pending.");

	}

	private void pendingRequests() {

	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {

				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				userService.AUTH_TOKEN = currentUser.getToken();
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

}
