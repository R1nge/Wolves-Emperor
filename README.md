Minecraft 1.21.5  
https://fabricmc.net/use/installer/  
java -jar fabric-installer-1.1.0.jar    

Install fabric-api to the mods folder    
https://www.curseforge.com/minecraft/mc-mods/fabric-api/download/6863346    

Install anaconda  
https://www.anaconda.com/download/success  

Create a conda env using .yml file  
conda env create -n wolves-emperor python=3.10.18  
conda install -c conda-forge libstdcxx-ng  
conda install PyAudio  
pip install pyaudio  
pip install openwakeword==0.6.0  
conda env update -f voice_attack.yaml  

Run this, find your mic, change the sample rate  
https://github.com/R1nge/Wolves-Emperor/blob/1.21/main.py#L43  
```
import pyaudio

audio = pyaudio.PyAudio()

for i in range(audio.get_device_count()):
    info = audio.get_device_info_by_index(i)
    print(f"Device {i}: {info['name']}, Max Input Channels: {info['maxInputChannels']}, Default Sample Rate: {info['defaultSampleRate']}")

audio.terminate()
```

sudo apt-get install libspeexdsp-dev    
pip install https://github.com/dscripka/openWakeWord/releases/download/v0.1.1/speexdsp_ns-0.1.2-cp310-cp310-linux_x86_64.whl    

~~sudo apt install jackd2 qjackctl~~
sudo apt-get install portaudio19-dev python3-pyaudio

Run the python server  
python main.py

Donwload the jar from releases or build from sources  
https://github.com/R1nge/Wolves-Emperor/releases  
Put the jar into  
/home/{user}/.minecraft/mods  

Tame a wolf  
Try saying Alexa  

Enjoy?
