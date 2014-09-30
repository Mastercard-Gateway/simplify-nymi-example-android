simplify-nymi-example-android
=====================
Example Android project (Android Studio) with [Nymi](http://dev.getnymi.com/) and [Simplify](https://www.simplify.com/commerce/docs) frameworks.


## Requirements
* Download [Nymi SDK & Emulator](http://developers.getnymi.com/sdk/index.html)
* Please register a developer account with [Simplify Commerce](https://www.simplify.com/commerce/login/signup) for API Keys
* If you are developing mobile applications, please run a server instance for running payment transactions where you have your public & private keys persisted.
    You can clone this [repo](https://github.com/simplifycom/simplify_payment_examples) and update the public & private keys in charge.php


## Steps

1. Open project using Android Studio
    *Note: If you are using Eclipse, please update the source & library paths according to your project settings*

2. Update SDK binaries under (https://github.com/simplifycom/simplify-nymi-example-android/tree/master/app/src/main/jniLibs/armeabi) based on your target environment
    *Note: This project was created on MacOS*

3. Update API Keys & Server URL values in [Macro.java] (https://github.com/simplifycom/simplify-nymi-example-android/tree/master/app/src/main/jniLibs/armeabi)






