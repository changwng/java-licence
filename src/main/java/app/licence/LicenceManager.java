package app.licence;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LicenceManager {

	public static final String FILE_APP_LIC = "app.lic";
	public static final String FILE_APP_SIG = "app.sig";
	
	public LicenceManager() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		LicenceManager lm = new LicenceManager();
		
		if (args[0].equals("generate_keys")) {
//			lm.generateKeys("security/pub.key", "security/priv.key");
			lm.generateKeys(args[1], args[2]);
			
		}
		else if (args[0].equals("generate_licence")) { 
//			lm.generateLicence("security/priv.key", 
//					"security", "app.zip",
//					"Hello World Company", "2017-12-31", "34-36-3B-C6-9C-5A");
//			
//			lm.zipLicence("security/app.lic", "security/app.sig", "security/app.zip");
			
			lm.generateLicence(args[1], 
					args[2]+"/"+LicenceManager.FILE_APP_LIC, args[2]+"/"+LicenceManager.FILE_APP_SIG,
					args[4], args[5], args[6]);
			lm.zipLicence(args[2]+"/"+LicenceManager.FILE_APP_LIC, args[2]+"/"+LicenceManager.FILE_APP_SIG, args[2]+"/"+args[3]);
			
		}
	}
	
	public byte[] getByteArrayFromZipFile(String zipPath, String fileName) throws Exception {
		byte[] buffer = new byte[1024];
		
		//get the zip file content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(zipPath)));
		//get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();
		byte[] byteArray = null;
		while(ze!=null){

			if(ze.getName().indexOf(fileName) != -1){				
				ByteArrayOutputStream out = null; // outside of your loop (for scope).

				out = new ByteArrayOutputStream(); 

				int bytesRead = 0;
				while ((bytesRead = zis.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				byteArray =  (out == null) ? null : out.toByteArray();
				out.close();
			}
			ze = zis.getNextEntry();
		}

	    zis.closeEntry();
		zis.close();
		return byteArray;
	}
	
	

	public boolean verify(String publicKeyResourcePath, String licenceZipPath) throws Exception {

	    byte[] byteLic = getByteArrayFromZipFile(licenceZipPath, LicenceManager.FILE_APP_LIC);
	    byte[] byteSig = getByteArrayFromZipFile(licenceZipPath, LicenceManager.FILE_APP_SIG);
	   
	    Signature sigApp = Signature.getInstance("MD5WithRSA");
		PublicKey keyPub = getPublicKey(publicKeyResourcePath);
		sigApp.initVerify(keyPub);
		sigApp.update(byteLic);

		return sigApp.verify(byteSig);
	}
	
	private void zipLicence(String licencePath, String signaturePath, String zipPath) {
		// TODO Auto-generated method stub

		try {
			FileOutputStream fos = new FileOutputStream(zipPath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			addToZipFile(licencePath, zos);
			addToZipFile(signaturePath, zos);
			zos.close();
			fos.close();
			System.out.println("Licence zipped");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName.substring(fileName.lastIndexOf("/")+1));
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	
	public void generateLicence(String privateKeyPath, 
								String licencePath, String signaturePath,
								String licence, String expiryDate, String macAddress) throws Exception {
		
		PrivateKey keyPriv = getPrivateKey(privateKeyPath);
		
		ProductKey pk = new ProductKey();
		pk.setLicence(licence);
		pk.setExpiryDate(expiryDate);
		pk.setMacAddress(macAddress);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
				
		String pkJsonString = gson.toJson(pk);

		byte[] licenceBytes = pkJsonString.getBytes("UTF8");

	    Signature sig = Signature.getInstance("MD5WithRSA");
	    sig.initSign(keyPriv);
	    sig.update(licenceBytes);
	    byte[] signatureBytes = sig.sign();
	    
	    writeStream(licenceBytes, licencePath);
	    writeStream(signatureBytes, signaturePath);
	    
	    System.out.println("Licence Generated");
	}

	public boolean checkMacAddress(String licencePath) {

		byte[] byteLic = getByteArrayFromFile(new File(licencePath));

	    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
		ProductKey pk2 = gson2.fromJson(new String(byteLic), ProductKey.class);
		
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
			if (pk2.getMacAddress().equals(macAddress)) {
				return true;
			}

		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void checkExpiryDate(String licencePath) throws ParseException {
		byte[] byteLic = getByteArrayFromFile(new File(licencePath));

	    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
		ProductKey pk2 = gson2.fromJson(new String(byteLic), ProductKey.class);

	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date today = new Date();
	    Date todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        
	    Date expiryDate = dateFormat.parse(pk2.getExpiryDate());
	    
	    if (expiryDate.equals(todayWithZeroTime)) {
	    	System.out.println("expiry date = today");
	    }
	    else if (expiryDate.after(todayWithZeroTime)) {
	    	System.out.println("expiry date > today");
	    }
	    else if (expiryDate.before(todayWithZeroTime)) {
	    	System.out.println("expiry date < today");
	    }

	}
	
	public boolean verify(String publicKeyResourcePath, String licencePath, String signaturePath) throws Exception {

	    byte[] byteLic = getByteArrayFromFile(new File(licencePath));
	    byte[] byteSig = getByteArrayFromFile(new File(signaturePath));
	   
	    Signature sigApp = Signature.getInstance("MD5WithRSA");
		PublicKey keyPub = getPublicKey(publicKeyResourcePath);
		sigApp.initVerify(keyPub);
		sigApp.update(byteLic);

		return sigApp.verify(byteSig);
	}
	
	private void writeStream(byte[] data, String filePath) throws IOException {
	    FileOutputStream stream = new FileOutputStream(new File(filePath));
	    try {
	    	stream.write(data);
	    } 
	    finally {
	    	stream.close();
	    }
	}
	
	public byte[] getByteArrayFromFile(File file) {
		byte[] byteArray  = new byte[(int) file.length()];
	    try {
	    	FileInputStream fileInputStream = new FileInputStream(file);
	    	fileInputStream.read(byteArray);
	    	fileInputStream.close();
	    	return byteArray;
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	    }
	    return null;
	}
	
	private PublicKey getPublicKey(String publicKeyResourcePath) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(publicKeyResourcePath).toPath());
		
		
//		URL r = this.getClass().getClassLoader().getResource(publicKeyResourcePath);
//		InputStream is = r.openStream();
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		int nRead;
//		byte[] data = new byte[16384];
//
//		while ((nRead = is.read(data, 0, data.length)) != -1) {
//		  buffer.write(data, 0, nRead);
//		}
//
//		buffer.flush();
//
//		byte[] keyBytes = buffer.toByteArray();
				
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PublicKey keyPub =  kf.generatePublic(spec);
	    return keyPub;
	}
	
	private PrivateKey getPrivateKey(String privateKeyPath) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    PrivateKey keyPriv =  kf.generatePrivate(spec);
	    return keyPriv;
	}
	
	public void generateKeys(String publicKeyPath, String privateKeyPath) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();

		/* save the public key in a file */
		byte[] keyPub = pub.getEncoded();
		FileOutputStream keyPubOs = new FileOutputStream(publicKeyPath);
		keyPubOs.write(keyPub);
		keyPubOs.close();
		
		byte[] keyPriv = priv.getEncoded();
		FileOutputStream keyPrivOs = new FileOutputStream(privateKeyPath);
		keyPrivOs.write(keyPriv);
		keyPrivOs.close();
		
		System.out.println("Keys Generated");
	}

}
