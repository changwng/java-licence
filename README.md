#Java Licence

## Objectives
1. The program is designed to run in an offline situation with licence control
2. It can be only executed on or before the expiry date
3. It can be only executed in a machine which the mac address of the machine is registered (Optional)

## Specifications

###Licence Encryption (Company)
1. Use the private key to encrypt the licence(app.lic), which the expiry date and the mac address of the client are stated. The output is a signature file(app.sig)
2. Zip the signature file together with licence file. (app.zip, which zips two files app.lic and app.sig])
3. Distribute the signature zip(app.zip) and the public key(pub.key) to the client

###Steps in terms of functions (Company) 
	1. Key Generation: KeyGen(Seed) -> PubKey + PrivKey
	2. Encryption: Encrypt(PrivKey, AppLic[ExpiryDate, MacAddress]) -> AppSig[Encrypted(ExpiryDate, MacAddress)]
	3. Send File thru email: Email(AppSig, PubKey) -> Client

###License Decryption (Client)
1. The client puts the signature zip(app.zip) and the public key(pub.key) in his/her computer
2. Before execution, the program will read the signature zip
3. The program will unzip the signature zip(app.zip -> app.sig + app.lic), then use the public key(pub.key) to decrypt the signature file(app.sig)
4. A successful decryption indicates that the signature file is an authorized file, which is generated by the company.
5. After the signature file is verified, the program will compare the decrypted content of the app.sig with the app.lic
6. If the content is identical, which means the program can use the content of the licence file to check the expiry date and the mac address
7. The expiry date checking is used to ensure the client can only execute the program on or before the expiry date
8. The mac address verification is used to ensure the client must execute the program provided

###Steps in terms of functions (Client)
	1. Signature: SigVerify(PubKey, AppSig), if verified -> go to Decryption, else stop
	2. Decryption: Decrypt(PubKey, AppSig) -> AppLic[ExpiryDate, MacAddress]
	3. Check Expiry Date: Compare(AppLic[ExpiryDate], Client[Time]), if AppLic[ExpiryDate] < Client[Time] -> Check Mac Address, else stop
	4. Check Mac Address: Compare(AppLic[MacAddress], Client[MacAddress]), if identical -> Start Program, else stop 
	
## Program Execution

### 1. Generate Public/Private Keys
	
mvn exec:java -Dexec.mainClass="app.licence.LicenceManager" -Dexec.args="generate_keys &lt;public key file path&gt; &lt;private key file path&gt;"

	MacBook-Pro:java-licence sysroot$ mvn exec:java -Dexec.mainClass="app.licence.LicenceManager" -Dexec.args="generate_keys security/pub.key security/priv.key"
	[INFO] Scanning for projects...
	[INFO]                                                                         
	[INFO] ------------------------------------------------------------------------
	[INFO] Building java-licence 0.0.1-SNAPSHOT
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ java-licence ---
	Keys Generated
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 1.054 s
	[INFO] Finished at: 2017-02-23T12:03:52+08:00
	[INFO] Final Memory: 9M/155M
	[INFO] ------------------------------------------------------------------------

### 2. Generate Licence

mvn exec:java -Dexec.mainClass="app.licence.LicenceManager" -Dexec.args="generate_licence &lt;private key file path&gt; &lt;licence folder path&gt; &lt;licence file name&gt; &lt;company name&gt; &lt;expiry date&gt; &lt;mac address&gt;"

	MacBook-Pro:java-licence sysroot$ mvn exec:java -Dexec.mainClass="app.licence.LicenceManager" -Dexec.args="generate_licence security/priv.key security app.zip 'Hello World Company' 2017-12-31 34-36-3B-C6-9C-5A"
	[INFO] Scanning for projects...
	[INFO]                                                                         
	[INFO] ------------------------------------------------------------------------
	[INFO] Building java-licence 0.0.1-SNAPSHOT
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ java-licence ---
	Licence Generated
	Writing 'security/app.lic' to zip file
	Writing 'security/app.sig' to zip file
	Licence zipped
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 0.788 s
	[INFO] Finished at: 2017-02-23T12:04:12+08:00
	[INFO] Final Memory: 9M/155M
	[INFO] ------------------------------------------------------------------------

### 3. Run Application with Public Key and Licence

mvn exec:java -Dexec.mainClass="app.licence.LicenceApplication" -Dexec.args="&lt;public key file path&gt; &lt;licence file path&gt;"

	MacBook-Pro:java-licence sysroot$ mvn exec:java -Dexec.mainClass="app.licence.LicenceApplication" -Dexec.args="security/pub.key security/app.zip"
	[INFO] Scanning for projects...
	[INFO]                                                                         
	[INFO] ------------------------------------------------------------------------
	[INFO] Building java-licence 0.0.1-SNAPSHOT
	[INFO] ------------------------------------------------------------------------
	[INFO] 
	[INFO] --- exec-maven-plugin:1.4.0:java (default-cli) @ java-licence ---
	Current IP address : 172.16.2.41
	Current MAC address : 34-36-3B-C6-9C-5A
	Licence Valid. Welcome to Hello World Company!
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 0.792 s
	[INFO] Finished at: 2017-02-23T12:04:35+08:00
	[INFO] Final Memory: 10M/220M
	[INFO] ------------------------------------------------------------------------

