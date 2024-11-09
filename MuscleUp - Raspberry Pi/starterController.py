import RPi.GPIO as gpio
from waveshare_OLED import OLED_0in96
from PIL import Image, ImageDraw, ImageFont
from MuscleUp import *
from threading import *
from datetime import datetime, date
import os, sys, time, socket, cv2, requests, signal, subprocess, bluetooth
from flask import Flask, Response, render_template_string, request
app = Flask(__name__)
picdir = os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(__file__))), 'pic')
libdir = os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(__file__))), 'lib')
if os.path.exists(libdir):
    sys.path.append(libdir)

skip_button, reset_button, next_button, led = 18, 6, 24, 13
gpio.setmode(gpio.BCM)
gpio.setwarnings(False)
gpio.setup(skip_button, gpio.IN)
gpio.setup(reset_button, gpio.IN)
gpio.setup(next_button, gpio.IN)
gpio.setup(led, gpio.OUT)
gpio.output(led, gpio.HIGH)
time.sleep(1)
gpio.output(led, gpio.LOW)

def get_local_ip():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.settimeout(0)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
    except Exception as e:
        ip = "Error: " + str(e)
    finally:
        s.close()
    return ip

def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

def generate_frames():
    camera = cv2.VideoCapture(0)
    camera.set(cv2.CAP_PROP_FPS, 24)
    while True:
        success, frame = camera.read()
        if gpio.input(next_button) and gpio.input(skip_button): 
            requests.post('http://127.0.0.1:5000/shutdown')
            break
        if not success:
            break
        else:
            ret, buffer = cv2.imencode('.jpg', frame)
            frame = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')
    camera.release()

@app.route('/')
def index():
    html = '''
    <!doctype html>
    <html lang="en">
      <head>
        <meta charset="utf-8">
        <title>Muscle Up Camera Feed</title>
        <style>
          body { background-color: #bd3050; color: white; text-align: center; }
          h1 { margin-top: 50px; font-size: 40px;}
        </style>
      </head>
      <body>
        <h1>Muscle Up Camera Feed</h1>
        <img src="{{ url_for('video_feed') }}" />
      </body>
    </html>
    '''
    return render_template_string(html)

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

@app.route('/shutdown', methods=['POST'])
def shutdown():
    shutdown_server()
    os.system("sudo reboot -h now")
    return 'Server shutting down...'

def run():
    global app
    app.run(host='0.0.0.0', port=5000, threaded=True)
    
def stripBl(data):
    dat = str(data)[2:]
    dat = dat[::-1][5:]
    dat = dat[::-1]
    return dat

class Connection:
    def __init__(self):
        self.connection = False
        self.script_end = False
        self.sem_send = Semaphore(0)
        self.sem_recv = Semaphore(0)
        
        self.server_socket=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
        port = 1
        self.server_socket.bind(("",port))
        self.server_socket.listen(1)
        self.data = ""
        self.event = Event()
        self.event2 = Event()

        self.send_thread = Thread(target=self.send, args=(self.event,))
        self.recv_thread = Thread(target=self.recv, args=(self.event2,))
        self.send_thread.daemon = True
        self.recv_thread.daemon = True
        
        self.recv_thread.start()
        self.send_thread.start()

        self.client_socket, self.address = self.server_socket.accept()
        self.connection = True
        self.sem_recv.release()
        self.sem_send.release()
        gpio.output(led, gpio.HIGH)
        time.sleep(1)
        gpio.output(led, gpio.LOW)
        print("Accepted connection from ", self.address)

    def recv(self, events2: Event) -> None:
        self.sem_recv.acquire()
        while True:
            try:
                self.data = self.client_socket.recv(1024)
            except:
                exit()
            self.data = stripBl(self.data)
            print("Received:", self.data)
            
        self.client_socket.close()
        self.server_socket.close()

    def send(self, event: Event) -> None:
        self.sem_send.acquire()
        while(not self.script_end):
            if "disconnect" in self.data:
                self.client_socket.close()
                self.server_socket.close()
                self.script_end = True
            else:
                try:
                    if self.data[0] == "S":
                        parts = self.data.split("|")
                        timee = int(parts[1])
                        self.data = parts[2]
                        data = ast.literal_eval(self.data)
                        m = MuscleUp(data, timee)
                        self.client_socket.send(m.data)
                        del m
                        time.sleep(1)
                        os.system("sudo shutdown -h now")
                except: pass

def main():
    ip = 0
    try:
        while True:
            if gpio.input(reset_button):
                c = Connection()
                while (not c.script_end):
                    if c.script_end:
                        pass
            if gpio.input(skip_button) and gpio.input(next_button):
                if ip == 0:
                    disp = OLED_0in96.OLED_0in96()
                    disp.Init()
                    disp.clear()
                    x = 20
                    while x >= 1:
                        image = Image.new("1", (disp.width, disp.height), "WHITE")
                        draw = ImageDraw.Draw(image)
                        draw.text((36,0), "Muscle Up", fill=0)
                        draw.text((0,17), "Website address: ", fill=0)
                        draw.text((0,32), f"{get_local_ip()}:5000", fill=0)
                        draw.text((0,47), f"Start in: {x}s", fill=0)
                        disp.ShowImage(disp.getbuffer(image))
                        time.sleep(1)
                        disp.clear()
                        x -= 1
                    disp.clear()
                    ip = 1
                run()
            time.sleep(1)
    except: pass
    finally:
        gpio.cleanup()

main()
