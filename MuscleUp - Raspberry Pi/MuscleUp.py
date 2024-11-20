import cv2
from waveshare_OLED import OLED_0in96
from gpiozero import Buzzer
from PIL import Image, ImageDraw, ImageFont
import RPi.GPIO as gpio
import mediapipe as mp
import numpy as np
import matplotlib.pyplot as plt
import time, traceback, os, sys, datetime, math, ast
picdir = os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(__file__))), "pic")
libdir = os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(__file__))), "lib")
if os.path.exists(libdir):
    sys.path.append(libdir)
gpio.setmode(gpio.BCM)
gpio.setwarnings(False)
try:
    def stripe(t):
        i = t.find("(")
        if i == -1: return t
        t = t[0:i-1]
        return t

    def calculate_angle(a,b,c):
        a = np.array(a)
        b = np.array(b)
        c = np.array(c)
        radians = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
        angle = np.abs(radians*180.0/np.pi)
        if angle > 180.0:
            angle = 360-angle
        return angle 

    class MuscleUp:
        def __init__(self, exercises, rest_time):
            gpio.setmode(gpio.BCM)
            reset_button, next_button, fan, modes, skip_button = 6, 24, 12, [0, 1], 18
            gpio.setup(reset_button, gpio.IN)
            gpio.setup(next_button, gpio.IN)
            gpio.setup(skip_button, gpio.IN)
            gpio.setwarnings(False)
            gpio.setup(fan, gpio.OUT)
            last_time, prev = time.time(), gpio.input(reset_button)
            indeks, self.__e, self.__k, repsDone, skip, self.st2, self.startBuzz = 1, 0, 0, 0, 1, 0, 0
            self.__ex, self.__reps, self.__ifpass, self.__stage = [], {}, 0, None
            ts, buzzer, self.__counter, self.data = time.time(), Buzzer(17), 0, ""
            self.__supported = ["crunches", "elbow plank", "full plank", "side plank", "leg raises", "L-sit", "hanging knee raises", "hanging leg raises", "push-ups", "diamond push-ups", "narrow push-ups", "wide push-ups", "incline push-ups", "decline push-ups", "dips", "chest flies", "pull-ups", "wide pull-ups", "narrow pull-ups", "chin-ups", "dead hang", "lateral raises", "shoulder press", "pike push-ups", "tricep dips", "bicep curls", "hammer curls", "squats", "elevated pike push-ups", "reverse crunches", "archer push-ups", "lunges", "step-ups", "bulgarian squats", "pistol squats", "frog stand", "wall handstand", "handstand", "body rows"]
            self.sup = lambda x: 1 if stripe(x) in self.__supported else 0
            p = gpio.PWM(fan, 100)
            p.start(100)
            for y in exercises:
                self.__ex.append(y[0])
                self.__reps[stripe(y[0])] = 0
                        
            self.__mp_drawing = mp.solutions.drawing_utils
            self.__mp_pose = mp.solutions.pose
            self.__cap = cv2.VideoCapture(0)

            try:
                self.__disp = OLED_0in96.OLED_0in96()
                self.__disp.Init()
                self.__disp.clear()
                self.__image1 = Image.new("1", (self.__disp.width, self.__disp.height), "WHITE")
                draw = ImageDraw.Draw(self.__image1)

                for y in exercises:
                    self.__counter, t, t2 = 0, 0, 0
                    self.__stage = None
                    with self.__mp_pose.Pose(
                        min_detection_confidence=0.7,
                        min_tracking_confidence=0.7) as pose:
                        while self.__cap.isOpened():
                            success, self.__image = self.__cap.read()
                            self.__image = cv2.resize(self.__image, (640, 480))
                            if not success:
                                continue

                            if self.startBuzz == 0:
                                buzzer.on()
                                time.sleep(0.1)
                                buzzer.off()
                                self.startBuzz = 1
                            ret, frame = self.__cap.read()
                            self.__image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                            self.__image.flags.writeable = False
                            self.__results = pose.process(self.__image)
                            self.__image.flags.writeable = True
                            self.__image = cv2.cvtColor(self.__image, cv2.COLOR_RGB2BGR)

                            if self.__ifpass == 1:
                                self.__ifpass = 0
                            if gpio.input(next_button) == 1:
                                self.__display(self.__counter, y[1], self.sup(y[0]))
                                t2 = 1
                            if gpio.input(skip_button) == 1:
                                skip += 1
                                t, t2 = 1, 0
                                self.__e += 1
                                if self.__e == len(self.__ex):
                                    self.__disp.clear()
                                    self.__k = 1
                                else:
                                    for index, item in enumerate(exercises):
                                        if item == y:
                                            try:
                                                self.__display(self.__counter, exercises[index+1][1], self.sup(y[0]))
                                            except:
                                                pass
                                break

                            button_state = gpio.input(reset_button)
                            if gpio.input(next_button) == 1 and self.__counter >= y[1]:
                                self.__e += 1
                                if self.__e == len(self.__ex):
                                    self.__disp.clear()
                                    self.__k = 1
                                else:
                                    self.__display(self.__counter, y[1], self.sup(y[0]))
                            if gpio.input(next_button) == 1 and button_state == 1 and self.__ifpass == 0:
                                self.__e += 1
                                if self.__e == len(self.__ex):
                                    self.__disp.clear()
                                    self.__k = 1
                                else:
                                    self.__display(self.__counter, y[1], self.sup(y[0]))
                                self.__ifpass = 1
                                
                            if button_state != prev:
                                prev = button_state
                                if button_state:
                                    for x in modes:
                                        if modes.index(x) == indeks:
                                            self.__display(self.__counter, y[1], self.sup(y[0]))
                                        else:
                                            self.__disp.clear()
                                    indeks += 1
                                    if indeks >= len(modes): indeks = 0
                            
                            """if self.__results.pose_landmarks == None:
                                cv2.putText(self.__image, "No person detected", 
                                            (50, 50), 
                                            cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2, cv2.LINE_AA)
                                cv2.imshow("Mediapipe", self.__image)
                                if cv2.waitKey(10) & 0xFF == ord('q'):
                                    break
                                continue"""
                                                
                            try:
                                landmarks = self.__results.pose_landmarks.landmark
                                shoulder = [landmarks[self.__mp_pose.PoseLandmark.LEFT_SHOULDER.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
                                elbow = [landmarks[self.__mp_pose.PoseLandmark.LEFT_ELBOW.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
                                wrist = [landmarks[self.__mp_pose.PoseLandmark.LEFT_WRIST.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_WRIST.value].y]
                                hip = [landmarks[self.__mp_pose.PoseLandmark.LEFT_HIP.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_HIP.value].y]
                                ankle = [landmarks[self.__mp_pose.PoseLandmark.LEFT_ANKLE.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_ANKLE.value].y]
                                knee = [landmarks[self.__mp_pose.PoseLandmark.LEFT_KNEE.value].x,landmarks[self.__mp_pose.PoseLandmark.LEFT_KNEE.value].y]
                                    
                                shoulder2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_SHOULDER.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_SHOULDER.value].y]
                                elbow2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_ELBOW.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_ELBOW.value].y]
                                wrist2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_WRIST.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_WRIST.value].y]
                                hip2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_HIP.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_HIP.value].y]
                                ankle2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_ANKLE.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_ANKLE.value].y]
                                knee2 = [landmarks[self.__mp_pose.PoseLandmark.RIGHT_KNEE.value].x,landmarks[self.__mp_pose.PoseLandmark.RIGHT_KNEE.value].y]   
                                            
                                if t2 == 1:
                                    if t == 0:
                                        buzzer.on()
                                        time.sleep(0.1)
                                        buzzer.off()
                                        t = 1
                                    
                                    if "bicep curls" in y[0]:
                                        angle = calculate_angle(shoulder, elbow, wrist)
                                        angle2 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        if angle > 160 and angle2 > 160:
                                            self.__stage = "down"
                                        if angle < 30 and angle2 < 30 and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                    
                                    elif "handstand" in y[0]:
                                        angle = calculate_angle(shoulder, elbow, wrist)
                                        angle2 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        shoulders_higher_than_elbows = (shoulder[1] < elbow[1]) and (shoulder2[1] < elbow2[1])
                                        hips_higher_than_shoulders = (hip[1] < shoulder[1]) and (hip2[1] < shoulder[1])

                                        if shoulders_higher_than_elbows and hips_higher_than_shoulders and angle > 150 and angle2 > 150:
                                            current_time = time.time()
                                            if current_time - last_time >= 1:
                                                self.__counterUp(y[1])
                                                last_time = current_time
                           
                                    elif ("push-ups" in y[0] and "tiger" not in y[0]) or "shoulder press" in y[0] or "dips" in y[0] or "body rows" in y[0]:
                                        angle = calculate_angle(shoulder, elbow, wrist)
                                        angle2 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        if angle < 80 and angle2 < 80:
                                            self.__stage = "down"
                                        if ("wide" in y[0] or "archer" in y[0]) and angle < 110 or angle2 < 110:
                                            self.__stage = "down"
                                        if angle > 140 and angle2 > 140 and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                            
                                    elif "plank" in y[0]:
                                        angle = calculate_angle(elbow, shoulder, hip)
                                        angle2 = calculate_angle(elbow2, shoulder2, hip2)
                                        angle3 = calculate_angle(shoulder, hip, knee)
                                        angle4 = calculate_angle(shoulder2, hip2, knee2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(shoulder, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(shoulder2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        cv2.putText(self.__image, str(round(angle3, 2)), 
                                                       tuple(np.multiply(hip, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle4, 2)), 
                                                       tuple(np.multiply(hip2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        p = 0 
                                        if "side" in y[0]:
                                            p = 1
                                        if ((angle < 100 and angle2 < 100) or ((angle < 110 or angle2 < 110) and p == 1)) and ((160 > angle3 > 110 and 160 > angle4 > 110) or ((170 > angle3 > 110 and 170 > angle4 > 110) and p == 1)):
                                            current_time = time.time()
                                            if current_time - last_time >= 1:
                                                self.__counterUp(y[1])
                                                last_time = current_time
                                                
                                    elif "lateral raises" in y[0]:
                                        angle = calculate_angle(hip, shoulder, elbow)
                                        angle2 = calculate_angle(hip2, shoulder2, elbow2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(shoulder, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(shoulder2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        if angle > 70 and angle2 > 70:
                                            self.__stage = "down"
                                        if angle < 40 and angle2 < 40 and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                            
                                    elif "chest flies" in y[0]:
                                        angle = calculate_angle(shoulder2, shoulder, elbow)
                                        angle2 = calculate_angle(shoulder, shoulder2, elbow2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(shoulder, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(shoulder2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        if angle > 130 and angle2 > 130:
                                            self.__stage = "down"
                                        if (angle < 90 or angle2 < 90) and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                            
                                    elif "pull-ups" in y[0] or "chin-ups" in y[0]:
                                        angle = calculate_angle(shoulder, elbow, wrist)
                                        angle2 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        shoulders_higher_than_elbows = (shoulder[1] < elbow[1]) and (shoulder2[1] < elbow2[1])

                                        if angle > 140 and angle2 > 140:
                                            self.__stage = "down"
                                        if angle < 80 and angle2 < 80 and self.__stage == "down" and shoulders_higher_than_elbows:
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                            
                                    elif "dead hang" in y[0]:
                                        angle = calculate_angle(shoulder, elbow, wrist)
                                        angle2 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        elbows_higher_than_shoulders = (shoulder[1] > elbow[1]) and (shoulder2[1] > elbow2[1])
                                        
                                        if elbows_higher_than_shoulders and angle > 150 and angle2 > 150:
                                            current_time = time.time()
                                            if current_time - last_time >= 1:
                                                self.__counterUp(y[1])
                                                last_time = current_time
                                                
                                    elif "hanging knee raises" in y[0] or "hanging leg raises" in y[0]:
                                        if "hanging knee raises" in y[0]:
                                            angle = calculate_angle(shoulder, hip, knee)
                                            angle2 = calculate_angle(shoulder2, hip2, knee2)
                                        elif "hanging leg raises" in y[0]:
                                            angle = calculate_angle(shoulder, hip, ankle)
                                            angle2 = calculate_angle(shoulder2, hip2, ankle2)
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(hip, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(hip2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        elbows_higher_than_shoulders = (shoulder[1] > elbow[1]) and (shoulder2[1] > elbow2[1])

                                        if angle > 140 and angle2 > 140:
                                            self.__stage = "down"
                                        if angle < 80 and angle2 < 80 and self.__stage == "down" and elbows_higher_than_shoulders:
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                            
                                    elif "L-sit" in y[0]:
                                        angle = calculate_angle(shoulder, hip, knee)
                                        angle2 = calculate_angle(shoulder2, hip2, knee2)
                                        angle3 = calculate_angle(shoulder, elbow, wrist)
                                        angle4 = calculate_angle(shoulder2, elbow2, wrist2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(hip, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(hip2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        cv2.putText(self.__image, str(round(angle3, 2)), 
                                                       tuple(np.multiply(elbow, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle4, 2)), 
                                                       tuple(np.multiply(elbow2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        hips_higher_than_wrists = (wrist[1] > hip[1]) or (wrist2[1] > hip2[1])
                                        
                                        if angle < 135 and angle2 < 135 and hips_higher_than_wrists and angle3 > 155 and angle4 > 155:
                                            current_time = time.time()
                                            if current_time - last_time >= 1:
                                                self.__counterUp(y[1])
                                                last_time = current_time
                                                
                                    elif "leg raises" in y[0]:
                                        angle = calculate_angle(shoulder, hip, ankle)
                                        angle2 = calculate_angle(shoulder2, hip2, ankle2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(hip, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(hip2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        if angle < 110 and angle2 < 110:
                                            self.__stage = "down"
                                        if angle > 160 and angle2 > 160 and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])  
                                          
                                    elif "frog stand" in y[0]:
                                        knees_higher_than_wrists = (wrist[1] > knee[1]) or (wrist2[1] > knee2[1])
                                        hips_higher_than_knees = (knee[1] > hip[1]) or (knee2[1] > hip2[1])
                                        hips_higher_than_shoulders = (shoulder[1] > hip[1]) or (shoulder2[1] > hip2[1])
                                        
                                        if knees_higher_than_wrists and hips_higher_than_knees and hips_higher_than_shoulders:
                                            current_time = time.time()
                                            if current_time - last_time >= 1:
                                                self.__counterUp(y[1])
                                                last_time = current_time
                                          
                                    elif "squats" in y[0] or "reverse crunches" in y[0]:
                                        angle = calculate_angle(hip, knee, ankle)
                                        angle2 = calculate_angle(hip2, knee2, ankle2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(knee, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(knee2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        if "bulgarian" in y[0] or "pistol" in y[0]:
                                            ag = 1
                                            if angle < 60 or angle2 < 60:
                                                self.__stage = "down"
                                                if angle < 60:
                                                    ag = 1
                                                elif angle2 < 60:
                                                    ag = 2
                                            if (angle > 140 or angle2 > 140) and self.__stage == "down":
                                                if (angle > 140 and ag == 1) or (angle2 > 140 and ag == 2):
                                                    self.__stage = "up"
                                                    self.__counterUp(y[1])
                                        else:
                                            if angle < 60 and angle2 < 60:
                                                self.__stage = "down"
                                            if angle > 140 and angle2 > 140 and self.__stage == "down":
                                                self.__stage = "up"
                                                self.__counterUp(y[1])
                                                
                                    elif "lunges" in y[0] or "step-ups" in y[0]:
                                        angle = calculate_angle(hip, knee, ankle)
                                        angle2 = calculate_angle(hip2, knee2, ankle2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(knee, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(knee2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        st = 0
                                        if "step-ups" in y[0]:
                                            st = 1
                                        an = 1
                                        if angle < 100 or angle2 < 100:
                                            self.__stage = "down"
                                            if angle < 100:
                                                an = 1
                                            elif angle2 < 100:
                                                an = 2
                                        if (angle > 140 or angle2 > 140) and self.__stage == "down":
                                            if (angle > 140 and an == 1) or (angle2 > 140 and an == 2):
                                                self.__stage = "up"
                                                if st == 1:
                                                    if self.st2 == 0:
                                                        self.__counterUp(y[1])
                                                        self.st2 = 1
                                                    else:
                                                        self.st2 = 0
                                                else:
                                                    self.__counterUp(y[1])

                                    elif "crunches" in y[0]:
                                        angle = calculate_angle(shoulder, hip, knee)
                                        angle2 = calculate_angle(shoulder2, hip2, knee2)
                                        
                                        cv2.putText(self.__image, str(round(angle, 2)), 
                                                       tuple(np.multiply(hip, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)
                                        
                                        cv2.putText(self.__image, str(round(angle2, 2)), 
                                                       tuple(np.multiply(hip2, [640, 480]).astype(int)), 
                                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 2, cv2.LINE_AA)

                                        if angle < 70 and angle2 < 70:
                                            self.__stage = "down"
                                        if angle > 100 and angle2 > 100 and self.__stage == "down":
                                            self.__stage = "up"
                                            self.__counterUp(y[1])
                                    else:
                                        while gpio.input(next_button) == 0:
                                            if gpio.input(next_button) == 1:
                                                self.__counter = int(y[1])
                                                t = 1
                                                break
                                    if self.__counter == int(y[1]) and t == 1:
                                        buzzer.on()
                                        time.sleep(0.3)
                                        buzzer.off()
                                        t = 2
                                        t2 = 0
                                        if (not (self.__e == len(self.__ex)-1)):
                                            if rest_time != 0:
                                                self.__disp.clear()
                                                x = rest_time
                                                while x >= 1:
                                                    self.__image1 = Image.new("1", (self.__disp.width, self.__disp.height), "WHITE")
                                                    draw = ImageDraw.Draw(self.__image1)
                                                    draw.text((36,0), "Muscle Up", fill=0)
                                                    draw.text((0,17), "Rest time:", fill=0)
                                                    draw.text((0,32), f"{x}s", fill=0)
                                                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                                                    time.sleep(1)
                                                    self.__disp.clear()
                                                    x -= 1
                                            self.__disp.clear()
                                            self.__e += 1
                                            self.__reps[stripe(y[0])] += self.__counter
                                            repsDone += self.__counter
                                            h = 0
                                            for index, item in enumerate(exercises):
                                                if y[0] == item[0]:
                                                    h = exercises[index+1][1]
                                            self.__display(0, h, self.sup(y[0]))
                                        else:
                                            self.__disp.clear()
                                            self.__e += 1
                                            self.__reps[stripe(y[0])] += self.__counter
                                            repsDone += self.__counter
                                        break
                                
                                """cv2.rectangle(self.__image, (0,0), (225,73), (245,117,16), -1)
                                cv2.putText(self.__image, str(self.__counter), 
                                            (10,60), 
                                            cv2.FONT_HERSHEY_SIMPLEX, 2, (255,255,255), 2, cv2.LINE_AA)
                                
                                self.__mp_drawing.draw_landmarks(self.__image, self.__results.pose_landmarks, self.__mp_pose.POSE_CONNECTIONS,
                                                        self.__mp_drawing.DrawingSpec(color=(245,117,66), thickness=2, circle_radius=2), 
                                                        self.__mp_drawing.DrawingSpec(color=(245,66,230), thickness=2, circle_radius=2) 
                                                         )               
                                
                                cv2.imshow("Mediapipe", self.__image)
                                if cv2.waitKey(10) & 0xFF == ord('q'):
                                    break"""
                                
                            except:
                                continue
                
                p.stop()
                cv2.destroyAllWindows()
                self.__cap.release()
                self.__disp.clear()
                ts1 = time.time()
                time_spent = int(ts1-ts)
                form = round(100 - ((skip-1) / len(self.__ex) * 100))
                self.data = f"A|{form}|{time_spent}|{repsDone}|{str(self.__reps)}"         
            
            except IOError as e:
                self.__disp.clear()
                self.__cap.release()
                gpio.cleanup()
                
            except KeyboardInterrupt:    
                self.__disp.module_exit()
                self.__disp.clear()
                cv2.destroyAllWindows()
                self.__cap.release()
                gpio.cleanup()
                exit()
                
        def __counterUp(self, z):
            self.__counter += 1
            self.__display(self.__counter, z)
            self.__counter2 = str(int(self.__counter))
        
        def __display(self, counter, y, d=1):
            name = self.__ex[self.__e]
            if not self.__e == len(self.__ex)-1:
                name2 = self.__ex[self.__e+1]
            else:
                name2 = "the end"
            try:
                if name[15] == "(":
                    name = name[0:14] + "."
                elif len(name) > 17:
                    if name[15] != " ":
                        name = name[0:16] + "."
                    else:
                        name = name[0:15] + "."
            except:
                pass
            
            try:
                if name2[13] == "(":
                    name2 = name2[0:12] + "."
                elif len(name2) > 15:
                    if name2[13] != " ":
                        name2 = name2[0:14] + "."
                    else:
                        name2 = name2[0:13] + "."
            except:
                pass
            if self.__e >= len(self.__ex):
                self.__disp.clear()
            elif self.__k == 0:
                self.__image1 = Image.new("1", (self.__disp.width, self.__disp.height), "WHITE")
                draw = ImageDraw.Draw(self.__image1)
                
                draw.text((36,0), "Muscle Up", fill=0)
                self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                if self.__e == len(self.__ex):
                    draw.text((0,17), "Now: ", fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                else:
                    draw.text((0,17), str("Now: " + name), fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                if d == 1:
                    draw.text((0,32), f"Done: {counter}/{y}", fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                else:
                    draw.text((0,32), f"Unsupported ({y})", fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                
                if self.__e == len(self.__ex)-1:
                    draw.text((0,47), "Next: the end", fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                elif self.__e == len(self.__ex):
                    draw.text((0,47), "Next: ", fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
                else:
                    draw.text((0,47), str("Next: " + name2), fill=0)
                    self.__disp.ShowImage(self.__disp.getbuffer(self.__image1))
except:
    pass
finally:
    gpio.cleanup()
