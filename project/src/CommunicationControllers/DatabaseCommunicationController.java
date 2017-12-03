package CommunicationControllers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DatabaseCommunicationController extends UnicastRemoteObject implements InterfaceDBController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DatabaseCommunicationController() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
}
