# Phone LED controller

Control your LEDs with your phone connected via WiFi to the Raspberry Pi

![aplication main screen](https://github.com/FilipGrela/Phone-led-controler/blob/main/images/main_screen.jpg | width=150)
![aplication settings screen](https://github.com/FilipGrela/Phone-led-controler/blob/main/images/settings_screen.jp | width=150)
# Requirements

Hardware Requirements:
1. Android phone version SDK >= 19
1. Router
1. LED strip
1. Raspberry Pi

## Instalation

To control the LEDs:
1. Download release.
2. Unzip downloaded file.
3. Install `led_controller.apk` on your phone.
    * To install application, you may be asked for permission to install applications from unknown sources.
4. Put `phone_led_controller.py` on Raspberry Pi.
5. Connecting the Raspberry Pi and the phone to a common network.


### Liblaries

To run the program you need to download all the required libraries, to do this enter the following comments in the command line on the Raspperry Pi

- install `python3.6`
- install LED library:
```sh
sudo pip install rpi_ws281x
```


### Preparation of Raspberry Pi:

To properly prepare the Raspberry Pi:
* connect the leds to the Rasperry Pi
* open the file `phone_controller.py` 
* correct amount of pixels your LED strip has
* correct GPIO pin 
* correct IP of Raspberry Pi in the router

### Run program

To run progra just type:
```sh
sudo python3 phone_controller.py
```
and open app on your phone.

###### Change IP or port in app 
To change IP and port just simply:
1. Open settings
2. Change IP
3. Change port

![Kitty!](https://media.giphy.com/media/vFKqnCdLPNOKc/giphy.gif)
