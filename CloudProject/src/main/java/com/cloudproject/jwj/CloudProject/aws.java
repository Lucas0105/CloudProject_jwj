package com.cloudproject.jwj.CloudProject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;

import java.util.Scanner;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class aws {
	static AmazonEC2 	ec2;
	
	private static void init() throws Exception {
		/*
		* The ProfileCredentialsProvider will return your [default]
		* credential profile by reading from the credentials file located at
		* (~/.aws/credentials).
		*/
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
		credentialsProvider.getCredentials();
		} catch (Exception e) {
		throw new AmazonClientException(
		"Cannot load the credentials from the credential profiles file. " +
		"Please make sure that your credentials file is at the correct " +
		"location (~/.aws/credentials), and is in valid format.",
		e);
		}
		ec2 = AmazonEC2ClientBuilder.standard()
		.withCredentials(credentialsProvider)
		.withRegion("us-east-2") /* check the region at AWS console */
		.build();
		}
	
	
	public static void main(String[] args) throws Exception {
		init();
		boolean systemCheck = true;
		Scanner menu = new Scanner(System.in);
		
		while(systemCheck)
		{
		System.out.println("		");
		System.out.println("		");
		System.out.println("------------------------------------------------------------");
		System.out.println("		Amazon AWS Control Panel using SDK		");
		System.out.println("		");
		System.out.println(" Cloud Computing, Computer Science Department		");
		System.out.println("		at Chungbuk National University ");
		System.out.println("------------------------------------------------------------");
		System.out.println(" 1. list instance		2. available zones		");
		System.out.println(" 3. start instance		4. available regions		");
		System.out.println(" 5. stop instance		6. create instance		");
		System.out.println(" 7. reboot instance		8. list images		");
		System.out.println(" 9. enable monitoring		10. disable monitoring	");
		System.out.println(" 11. terminate instance		99. quit		");
		System.out.println("------------------------------------------------------------");
		System.out.print("Enter an integer: ");
		int number = menu.nextInt();
		switch(number) {
		case 1:
			listInstances();
		break;
		
		case 2:
			availableZones();
		break;
		
		case 3:
			startInstance();
		break;
		
		case 4:
			availableRegions();
		break;
		
		case 5:
			stopInstance();
		break;
		
		case 6:
			createInstance();
		break;
		
		case 7:
			rebootInstance();
		break;
		
		case 8:
			listImage();
		break;
	
		case 9:
			enableMonitoring();
		break;
		
		case 10:
			disableMonitoring();
		break;
		case 11:
			terminateInstance();
		break;
		
		case 99:
			systemCheck = false; 
			System.out.println("Quit AWS Control Panel");
		break;
		}
		}
		menu.close();
	}
	
//	function1
	public static void listInstances()
	{
	System.out.println("Listing instances....");
	boolean done = false;
	DescribeInstancesRequest request = new DescribeInstancesRequest();
	while(!done) {
	DescribeInstancesResult response = ec2.describeInstances(request);
	for(Reservation reservation : response.getReservations()) {
	for(Instance instance : reservation.getInstances()) {
	System.out.printf(
	"[id] %s, " +
	"[AMI] %s, " +
	"[type] %s, " +
	"[state] %10s, " +
	"[monitoring state] %s",
	instance.getInstanceId(),
	instance.getImageId(),
	instance.getInstanceType(),
	instance.getState().getName(),
	instance.getMonitoring().getState());
	}
	System.out.println();
	}
	request.setNextToken(response.getNextToken());
	if(response.getNextToken() == null) {
	done = true;
	}
	}
	}
//	function2
	public static void availableZones() {
		System.out.print("Available zones....\n");
		DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest();
		DescribeAvailabilityZonesResult result = ec2.describeAvailabilityZones(request);
		int cnt = 0;
		
		for (AvailabilityZone zone : result.getAvailabilityZones())
		{
			cnt ++;
			System.out.printf(
					"[id] %s, " +
					"[region] %s, " +
					"[zone] %s\n",
					zone.getZoneId(),
					zone.getRegionName(),
					zone.getZoneName());
		}

		System.out.printf("You have access to %d Availability Zones.", cnt);
		
	}
	
//	function 3
	public static void startInstance()
	{
		Scanner id_string = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String instanceId = id_string.next();
		System.out.printf("starting....%s\n", instanceId);


		StartInstancesRequest request = new StartInstancesRequest()
		    .withInstanceIds(instanceId);
		try {
		ec2.startInstances(request);
		System.out.printf("Successfully started instance %s\n", instanceId);
		}catch(Exception e) {
			System.out.println(e);
		}
		id_string.close();
	}

//	function 4
	public static void availableRegions()
	{
		System.out.println("Available reions ....");
		DescribeRegionsResult result = ec2.describeRegions();
		
		for (Region region : result.getRegions()){
			{
				System.out.printf(
					"[region] %s, " + 
					"[endpotin] %s\n",
					region.getRegionName(),
					region.getEndpoint()
				);
			}
		}
	}
	
//	function 5
	public static void stopInstance()
	{
		Scanner id_string = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String instanceId = id_string.next();
		StopInstancesRequest request = new StopInstancesRequest()
			    .withInstanceIds(instanceId);
		try {
			ec2.stopInstances(request);
			System.out.printf("Successfully stop instance %s\n", instanceId);
		}catch(Exception e) {
			System.out.println(e);
		}
		id_string.close();
	}
	
//function6
	public static void createInstance()
	{
		Scanner id_string = new Scanner(System.in);
		System.out.print("Enter ami id: ");
		String amiId = id_string.next();
		
		RunInstancesRequest request = new RunInstancesRequest()
				.withImageId(amiId)
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName("awskey")
				.withSecurityGroups("htcondor-security")
				.withInstanceType("t2.micro");
		
		try {
			RunInstancesResult result = ec2.runInstances(request);
			String instanceId = result.getReservation().getInstances().get(0).getInstanceId();
			System.out.printf("Successfully started EC2 instance %s based on AMI %s\n",instanceId, amiId);
		}catch(Exception e) {
			System.out.println(e);
		}
		id_string.close();
	}
	
//	function 7
	public static void rebootInstance()
	{
		Scanner id_string = new Scanner(System.in);
		System.out.print("Enter instance id: ");
		String instanceId = id_string.next();
		
		System.out.printf("Rebooting ... %s\n", instanceId);
		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instanceId);
		
		try {
			ec2.rebootInstances(request);
			System.out.printf("Successfully reboot instance %s\n", instanceId);
		}catch(Exception e) {
			System.out.println(e);
		}
		id_string.close();
	}
	
	//function8
		public static void listImage() throws IOException
		{
			System.out.println("Listing images....");
			String propFile = "config/config.properties";
			Properties prop = new Properties();
			FileInputStream fis = new FileInputStream(propFile);
			prop.load(new java.io.BufferedInputStream(fis));
			
			
			DescribeImagesRequest request = new DescribeImagesRequest().withOwners(prop.getProperty("aws_id"));
			
			DescribeImagesResult response = ec2.describeImages(request);
			
			for(Image image : response.getImages()) {
			
			System.out.printf(
			"[ImageID] %s, " +
			"[Name] %s, " +
			"[Owner] %s",
			image.getImageId(),
			image.getName(),
			image.getOwnerId());
			System.out.println();
			}
			}
//		function9
		public static void enableMonitoring() {
			Scanner id_string = new Scanner(System.in);
			System.out.print("Enter instance id: ");
			String instanceId = id_string.next();
			
			MonitorInstancesRequest request = new MonitorInstancesRequest().withInstanceIds(instanceId);
			try {
				ec2.monitorInstances(request);
				System.out.printf("Successfully enable monitoring instance %s\n", instanceId);
			} catch (Exception e) {
				System.out.printf("%s", e);
			}
			id_string.close();
			
	
		}
//		function10
		public static void disableMonitoring() {
			Scanner id_string = new Scanner(System.in);
			System.out.print("Enter instance id: ");
			String instanceId = id_string.next();
			
			UnmonitorInstancesRequest request = new UnmonitorInstancesRequest().withInstanceIds(instanceId);
			try {
				ec2.unmonitorInstances(request);
				System.out.printf("Successfully disable monitoring instance %s\n", instanceId);
			} catch (Exception e) {
				System.out.printf("%s", e);
			}
			id_string.close();
		}
//		function11
		public static void terminateInstance() {
			Scanner id_string = new Scanner(System.in);
			System.out.print("Enter instance id: ");
			String instanceId = id_string.next();
			
			TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(instanceId);
			
			try {
				ec2.terminateInstances(request);
				System.out.printf("Successfully terminate instance %s\n", instanceId);
			} catch (Exception e) {
				System.out.printf("%s", e);
			}
			id_string.close();
		}
}