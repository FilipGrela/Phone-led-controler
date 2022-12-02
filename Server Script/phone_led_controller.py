import socket
from rpi_ws281x import *
import colorsys
import threading
import os

SERVER = "192.168.1.22"
PORT = 6061
ADDR = (SERVER, PORT)
HEADER = 8
FORMAT = 'utf-8'
EXIT_MSG = "fW;X#.`V:BhGg&-UT*?'+}FxY]c9<%S3w![Kk\"/E2rHNzD)QqM"
SHUTDOWN_MSG = "g3VH$wPq~2?(;MhF#5vaG{/\"eUAf]+`ynCd8z!DJxcXsSj=r)%"
DISCONNECT_MESSAGE = "!DISCONNECT"

LED_COUNT = 300      # Number of LED pixels.
LED_PIN = 18      # GPIO pin connected to the pixels (18 uses PWM!).
LED_FREQ_HZ = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA = 10      # DMA channel to use for generating signal (try 10)
LED_BRIGHTNESS = 60     # Set to 0 for darkest and 255 for brightest
# True to invert the signal (when using NPN transistor level shift)
LED_INVERT = False
LED_CHANNEL = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53

strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ,
                          LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL)
strip.begin()

os.system('lsof -i :6061')
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(ADDR)

h_value = 0
s_value = 100
v_value = 50


def handle_client(conn, addr, h_value, s_value, v_value):
    print(f"[NEW CONNECTION] {addr} connected")
    connected = True
    while connected:
        msg_length = conn.recv(HEADER).decode(FORMAT)
        if msg_length:
            msg_length = int(msg_length)
            msg = conn.recv(msg_length).decode(FORMAT)
            print(f"[{addr}] {msg}")
            handle_msg(msg, h_value, s_value, v_value)

            if msg == DISCONNECT_MESSAGE:
                connected = False
            elif msg == EXIT_MSG:
                print("\n---\nProgram exits. \nReason: EXIT_MSG received")
                os._exit(0)
            elif msg == SHUTDOWN_MSG:
                print("\n---\nProgram exits. \nReason: SHUTDOWN_MSG received")
                os.system("shutdown 15 -h")
                os._exit(0)

    conn.close()
    print(f"[{addr}] Connection closed")


def start():
    server.listen()
    print(f"[LISTENING] Server is listening on {SERVER}:{PORT}")
    while True:
        conn, addr = server.accept()
        thread = threading.Thread(target=handle_client, args=(
            conn, addr, h_value, s_value, v_value))
        thread.start()
        print(f"[ACTIVE CONNECTIONS] {threading.active_count() - 1}")


def hsv2rgb(h, s, v):
    if h != 0:
        h = h/360
    if s != 0:
        s = s/100
    if v != 0:
        v = v/100
    hsv = tuple(round(i * 255) for i in colorsys.hsv_to_rgb(h, s, v))
    return Color(hsv[0], hsv[1], hsv[2])


def leds_set_color(color, sleep=0):
    for i in range(150):
        strip.setPixelColor(149-i, color)
        strip.setPixelColor(150+i, color)
    strip.show()


def handle_msg(msg, h_value, s_value, v_value):

    if msg.startswith("H_VAL"):
        separated_values = [msg.split("H_VAL")[1].split("S_VAL")[0], msg.split("H_VAL")[1].split(
            "S_VAL")[1].split("V_VAL")[0], msg.split("H_VAL")[1].split("S_VAL")[1].split("V_VAL")[1]]

        leds_set_color(hsv2rgb(float(separated_values[0]), float(
            separated_values[1]), float(separated_values[2])))


try:
    leds_set_color(hsv2rgb(h_value, s_value, v_value))
    print("[STARTING] Server is starting....")
    start()
except KeyboardInterrupt:
    print("STOP")
