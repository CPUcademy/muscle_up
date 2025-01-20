from customtkinter import *
from tkinter import *
import ast, math, random

class Cipher:
    def __init__(self):
        self.symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 !?.ĄĆĘŁÓŻŹąćęłóżź{}'"

    @staticmethod
    def findReverse(a, m):
        if math.gcd(a, m) != 1: return None
        u1, u2, u3 = 1, 0, a
        v1, v2, v3 = 0, 1, m
        while v3 != 0:
            q = u3 // v3
            v1, v2, v3, u1, u2, u3 = (u1 - q * v1), (u2 - q * v2), (u3 - q * v3), v1, v2, v3
        return u1 % m

    def fragmentsOfKeys(self, key):
        keyA = key // len(self.symbols)
        keyB = key % len(self.symbols)
        return keyA, keyB

    def code(self, key, message):
        keyA, keyB = self.fragmentsOfKeys(key)
        ciphertext = ""
        for symbol in message:
            if symbol in self.symbols:
                symbolIndex = self.symbols.find(symbol)
                ciphertext += self.symbols[(symbolIndex * keyA + keyB) % len(self.symbols)]
            else:
                ciphertext += symbol
        return ciphertext

    def decode(self, key, message):
        keyA, keyB = self.fragmentsOfKeys(key)
        text1 = ""
        reverseOfKeyA = self.findReverse(keyA, len(self.symbols))
        for symbol in message:
            if symbol in self.symbols:
                symbolIndex = self.symbols.find(symbol)
                text1 += self.symbols[(symbolIndex - keyB) * reverseOfKeyA % len(self.symbols)]
            else:
                text1 += symbol
        return text1

    def randomKey(self):
        while True:
            keyA = random.randint(2, len(self.symbols))
            keyB = random.randint(2, len(self.symbols))
            if math.gcd(keyA, len(self.symbols)) == 1:
                return keyA * len(self.symbols) + keyB


class Config:
    def __init__(self):
        self.window = CTk()
        self.window.title("Muscle Up WiFi configurator")
        self.window.geometry("350x180+680+320")
        self.window.after(201, lambda :self.window.iconbitmap("logo.ico"))
        self.__name = "x"
        self.__password = "b"
        self.__networks = {}
        self.__c = Cipher()
        self.__code = int(self.__c.decode(3782, "gBuZ"))
        self.__setup()

        self.add = CTkButton(self.window, text="Add / edit a network", width=150, height=48, command=self.__addNetwork)
        self.add.place(relx=0.02, rely=0.03)
        self.delete = CTkButton(self.window, text="Delete a network", width=150, height=48, command=self.__deleteNetwork)
        self.delete.place(relx=0.02, rely=0.33)
        self.gen = CTkButton(self.window, text="Upload the config file", width=150, height=48, command=self.__generate)
        self.gen.place(relx=0.02, rely=0.63)

        self.myListbox = Listbox(self.window, font=("Arial", 15))

        self.__updateListbox()

        self.myListbox.place(relx=0.47, rely=0.03, width=270, height=236)

        self.window.mainloop()

    def __updateListbox(self):
        self.myListbox.delete(0, END)
        print(self.__networks)
        if self.__networks != {}:
            for x in self.__networks.keys():
                self.myListbox.insert(END, str(x))

    def __generate(self):
        for i in self.myListbox.curselection():
            self.__name = self.myListbox.get(i)
        self.__password = self.__networks[self.__name]
        try:
            f = open("D:/wpa_supplicant.conf", "w", encoding="utf8")
        except:
            pass
        opBr = "{"
        svBr = "}"
        text = f"""country=US
ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
update_config=1

network={opBr}
scan_ssid=1
ssid="{self.__name}"
psk="{self.__password}"
{svBr}
        """
        f.write(text)
        f.close()

    def __addNetwork(self):
        self.windowAdd = CTk()
        self.windowAdd.title("Add / edit a network")
        self.windowAdd.geometry("190x180+820+450")
        self.windowAdd.resizable(False, False)
        self.windowAdd.after(201, lambda: self.windowAdd.iconbitmap("logo.ico"))

        CTkLabel(self.windowAdd, text="Name").pack()
        name = CTkEntry(self.windowAdd)
        name.pack()
        CTkLabel(self.windowAdd, text="Password").pack()
        password = CTkEntry(self.windowAdd)
        password.pack()

        def __confirmAdding():
            self.__networks[str(name.get())] = str(password.get())
            self.__confirm()

        CTkButton(self.windowAdd, text="Confirm", command=__confirmAdding).pack(pady=20)

        self.windowAdd.mainloop()

    def __deleteNetwork(self):
        self.windowDelete = CTk()
        self.windowDelete.title("Delete a network")
        self.windowDelete.geometry("190x120+820+500")
        self.windowDelete.resizable(False, False)
        self.windowDelete.after(201, lambda :self.windowDelete.iconbitmap("logo.ico"))

        CTkLabel(self.windowDelete, text="Name").pack()
        name = CTkEntry(self.windowDelete)
        name.pack()

        def __confirmDeleting():
            try:
                del self.__networks[str(name.get())]
                self.__confirm()
            except:
                pass

        CTkButton(self.windowDelete, text="Confirm", command=__confirmDeleting).pack(pady=15)

        self.windowDelete.mainloop()

    def __confirm(self):
        self.__saveWiFiNetworks()
        self.__updateListbox()

    def __saveWiFiNetworks(self):
        f = open("networks.txt", "w", encoding="utf8")
        f.write(self.__c.code(self.__code, str(self.__networks)))
        f.close()

    def __setup(self):
        try:
            f = open("networks.txt", "r", encoding="utf8")
            self.__networks = ast.literal_eval(self.__c.decode(self.__code, f.read()))
            f.close()
        except:
            self.__networks = {}


if __name__ == "__main__":
    Config()
