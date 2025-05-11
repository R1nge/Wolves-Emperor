import pyaudio
import numpy as np
import openwakeword
from openwakeword.model import Model
import argparse
import time
import threading
import os
import win32file
import win32pipe

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

read_fd, write_fd = os.pipe()

def handle_client(hPipe):
    """Function to handle client communication."""
    while True:
        try:
            # Read data from the pipe
            hr, data = win32file.ReadFile(hPipe, 64*1024)  # Read up to 64 KB
            if data:
                print("Received:", data.decode())
        except Exception as e:
            print("Error reading from pipe:", e)
            break

def create_named_pipe():
    pipe_name = r'\\.\pipe\minecraft\wolvesEmperor'
    print("Creating named pipe...")

    # Create the named pipe
    hPipe = win32pipe.CreateNamedPipe(
        pipe_name,
        win32pipe.PIPE_ACCESS_DUPLEX,
        win32pipe.PIPE_TYPE_MESSAGE | win32pipe.PIPE_READMODE_MESSAGE | win32pipe.PIPE_WAIT,
        1,  # Maximum instances
        512,  # Output buffer size
        512,  # Input buffer size
        0,  # Client time-out
        None  # Default security attributes
    )

    print("Waiting for client to connect...")
    win32pipe.ConnectNamedPipe(hPipe, None)
    print("Client connected.")

    # Start a thread to handle client communication
    #client_thread = threading.Thread(target=handle_client, args=(hPipe,))
    #client_thread.start()

    last_activation_time = 0

    while True:
        # Read audio data
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
            
            # Write to the named pipe
            try:
                win32file.WriteFile(hPipe, b'true\n')
                print("Sent: true")
                last_activation_time = current_time
            except Exception as e:
                print("Error writing to pipe:", e)
                break  # Break inner loop to reconnect
            except Exception as e:
                print("Error writing to pipe:", e)

    try:
        win32file.DisconnectNamedPipe(hPipe)
    except:
        pass

    # Close the pipe (this line will not be reached if the loop runs indefinitely)
    


# Run capture loop continuosly, checking for wakewords
if __name__ == "__main__":
    #threading.Thread(target=create_named_pipe, daemon=True).start()
    create_named_pipe()