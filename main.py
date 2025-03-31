import pyaudio
import numpy as np
import openwakeword
from openwakeword.model import Model
import argparse
import time

openwakeword.utils.download_models()


# Parse input arguments
parser=argparse.ArgumentParser()
parser.add_argument(
    "--chunk_size",
    help="How much audio (in number of samples) to predict on at once",
    type=int,
    default=1280,
    required=False
)
parser.add_argument(
    "--model_path",
    help="The path of a specific model to load",
    type=str,
    default="",
    required=False
)
parser.add_argument(
    "--inference_framework",
    help="The inference framework to use (either 'onnx' or 'tflite'",
    type=str,
    default='onnx',
    required=False
)

args=parser.parse_args()

# Get microphone stream
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 16000
CHUNK = args.chunk_size
audio = pyaudio.PyAudio()
mic_stream = audio.open(format=FORMAT, channels=CHANNELS, rate=RATE, input=True, frames_per_buffer=CHUNK)

# Load pre-trained openwakeword models
if args.model_path != "":
    owwModel = Model(wakeword_models=[args.model_path], inference_framework=args.inference_framework)
else:
    owwModel = Model(inference_framework=args.inference_framework)

n_models = len(owwModel.models.keys())

last_activation_time = 0

# Run capture loop continuosly, checking for wakewords
if __name__ == "__main__":
    # Generate output string header
    print("\n\n")
    print("#"*100)
    print("Listening for wakewords...")
    print("#"*100)
    print("\n"*(n_models*3))

    

    while True:
        audio = np.frombuffer(mic_stream.read(CHUNK), dtype=np.int16)

        prediction = owwModel.predict(audio)

        mdl = next(iter(owwModel.prediction_buffer.keys()))
        scores = list(owwModel.prediction_buffer[mdl])
        curr_score = format(scores[-1], '.20f').replace("-", "")

        print(curr_score)
        current_time = time.time()

        if float(curr_score) >= 0.90:
            
            if current_time - last_activation_time < 10:
                continue
            absolute_path = "E:/MyMods/TEST.txt"
            with open(absolute_path, 'w') as file:
                file.write('true')
            print("true")
            time.sleep(2)
            #with open(absolute_path, 'w') as file:
            #    file.write('false')
            print("false")
            last_activation_time = current_time