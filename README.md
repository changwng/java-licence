#Java Licence

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
