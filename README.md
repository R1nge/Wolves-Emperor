Minecraft 1.21.5  
https://fabricmc.net/use/installer/  
java -jar fabric-installer-1.1.0.jar  

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

Run the python server  
python main.py

Donwload the jar from releases or build from sources  
https://github.com/R1nge/Wolves-Emperor/releases  
Put the jar into  
/home/{user}/.minecraft/mods  

Tame a wolf  
Try saying Alexa  

Enjoy?
