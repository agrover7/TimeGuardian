# TimeGuardian

WIP as of 3/30/18
Project created in tandem with Safetrek.
Authors: Jack Briody, Aman Grover

Objective: We designed an Android application that can be used to combat sexual assault. In environments when a user is feeling uncomfortable or threatened, they will set a timer with our application. Should they terminate the timer before it expires, then nothing will occur. However, if they fail to turn off the timer before its expiration, an alarm will sound. Simultaneously, the SafeTrek API will be utilized so that the appropriate modes of contacting the user (call and text) will be engaged. 

Purpose: Sexual assault can often surprise victims. By having an application that is there to protect them, no matter the context of their discomfort/fear, users will be able to feel more secure wherever they are. 

Implementation: Users will be prompted one time to log in to the SafeTrek API, using OAuth2 over SSL for authentication and authorization. From there, they will be directed to a screen with an explanation of the directions and a timer, which they could set to any time they choose. The timer will be assigned a sound to ring an alarm when it expires. Once the timer has expired, the user will be prompted if they would like to cancel or refresh the alarm. Should they fail to respond within 30 seconds, the SafeTrek API will be utilized to reach out for help.

Built With: Android Studio
