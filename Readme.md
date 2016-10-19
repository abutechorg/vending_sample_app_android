## Requirements:
1. Android SDK 4.4+ (api version 19) device

### Steps required to import to your android project

1. Import **blecommj** and **unattendedsdk** aar
2. Declare a class implementing interface **VendControllerCallback** and implements the overriding methods
3. Declare a member of class **VendController**
4. And you are good to go

### 1. Import **blecommj** and **unattendedsdk** aar

1. Go to the link https://developer.android.com/studio/projects/android-library.html#AddDependency
2. Make sure that you import both aar that are provided in this project
  1. blecommj/blecommj-release.aar
  2. unattendedsdk/Unattendedsdk-release.aar

### 2. Declare a class implementing interface **VendControllerCallback** and implements the overriding methods

Following the class **VendingPresenter**, implement the interface **VendControllerCallback** and implements the following methods

1. ```void connected()```
invoked on the event of successfully connected to targeted machine

2. ```void authRequest()```
invoked when there is interaction with the machine that requires funds to proceed to further action

3. ```void processCompleted()```
invoked on the event of vending success | failure | unable to connect | unable to locate machine

4. ```void disconnected()```
invoked on the event of disconnection between the target connected machine and mobile

5. ```void timeoutWarning()```
will be invoke when the targeted connected machine have invoked a timeout signal

6. ```Activity getActivity()```
to provide the current active activity

### 3. Declare a member of class **VendController**
This is the point of interaction in which the mobile app will use to start connection and send messages.
Before connecting to the machine there are three values that needs to be retrieved from the list machines api,
	- Machine Serial Number
	- Machine Model Number
	- Machine Service Id

1. For the demo app we are retrieving them from our partner server,
which the partner server is querying mastercard vending api to get the list of vending machines.
It is required to have a partner server to communicate with Vending api

2. In **VendingPresenter** you see the following declaration

	```
	this.mVendController = VendControllerFactory.newInstance(String vend_controller_type, VendControllerCallback callback,
	                String serialNum, String modelNum, String serviceId, int amount, String[] config);
	```
	This creates a VendController and attach it to the desired placeholder.

	Parameters are as follows
	```
	- vend_controller_type          //determine what type of vendcontroller to use VendControllerImpl | MockVendControllerImpl
	- callback                      //for an object to receive the response from machine
	- serialNum                     //machine serial number
	- modelNum                      //machine model number
	- serviceId                     //machine service id
	- amount                        //for the max amount that the mobile transaction
	- config                        //for mocking failures
	```

3. Start connection
Call this method `mVendController.connect()` after providing appropriate parameters to the constructor

4. In order to message the machine that you have finished payment and to proceed to vending,
Please get the payload from the validation service via the partner server and attach it to this method
so that the machine will start vending `mVendController.approveAuth(String payload)

**Development testing**

To run the mobile app without the simulator or bluetooth interaction, you can use the **MockVendControllerImpl**.

Just put **vend_controller_type** as either of the following:
    - Real vend controller: `VendControllerFactory.VEND_CONTROLLER_TYPE`
    - Mocked vend controller: `VendControllerFactory.VEND_CONTROLLER_TYPE_MOCK`

For failures you want to expect in the mocking you can put inside config parameter of the constructor. They can be any 1 of the following values
    - `MOCK_VENDING_FAILURE`, failure occcurs during vending
    - `MOCK_CONNECTED_FAILURE`, failure occurs during connection
    - `MOCK_SCANNING_FAILURE`, failure occurs during scanning
If you are not expecting any errors then you can pass an empty array.
