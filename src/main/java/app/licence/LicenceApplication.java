package app.licence;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class LicenceApplication {

	public LicenceApplication() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		LicenceManager lm = new LicenceManager();
		String licenceZipPath = args[1];
		
		if (lm.verify(args[0], licenceZipPath)) {
		    byte[] byteLic = lm.getByteArrayFromZipFile(licenceZipPath, LicenceManager.FILE_APP_LIC);
		    
		    Gson gson = new GsonBuilder().setPrettyPrinting().create();
			ProductKey pk = gson.fromJson(new String(byteLic), ProductKey.class);

		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    Date today = new Date();
		    Date todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
	        
		    Date expiryDate = dateFormat.parse(pk.getExpiryDate());
		    
		    if (expiryDate.equals(todayWithZeroTime) || expiryDate.after(todayWithZeroTime)) {
				
				InetAddress ip;
				try {

					ip = InetAddress.getLocalHost();
					System.out.println("Current IP address : " + ip.getHostAddress());

					NetworkInterface network = NetworkInterface.getByInetAddress(ip);

					byte[] mac = network.getHardwareAddress();

					System.out.print("Current MAC address : ");

					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
					}
					System.out.println(sb.toString());
					String macAddress = sb.toString();
					if (pk.getMacAddress().equals(macAddress)) {
						System.out.println("Licence Valid. Welcome to Hello World Company!");
					}
					else {
						 System.out.println("Mac address mismatch. Not allowed to use in this computer");
					}

				}
				catch (UnknownHostException e) {
					e.printStackTrace();
				}
				catch (SocketException e) {
					e.printStackTrace();
				}
				
		    }
		    else if (expiryDate.before(todayWithZeroTime)) {
		    	System.out.println("Licence Expired");
		    	try {
		    		System.out.println("\nPress ENTER to quit.");
		            System.in.read();
		            System.exit(0);
		        } 
		    	catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		    }
		}
		else {
			System.out.println("Invalid Licence");
	    	try {
	    		System.out.println("\nPress ENTER to quit.");
	            System.in.read();
	            System.exit(0);
	        } 
	    	catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		}
	}

}
