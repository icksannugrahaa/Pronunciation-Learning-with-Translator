import random
import os

from flask import Flask, request, jsonify
from stt_service import STT_Service

app = Flask(__name__)

@app.route("/predict", methods=["POST"])
def predict():
    # Get POST request and save the file
    audio_file = request.files["file"]
    file_name = str(random.randint(0, 100000))
    audio_file.save(file_name)

    #instiantiat STT_Service and predict the audio file
    stt = STT_Service()
    predicted_sentence = stt.predict(file_name)

    #delete audio file
    os.remove(file_name)

    #send predicted sentence in json format
    result = {"word": predicted_sentence}
    return jsonify(result)

if __name__ == "__main__":
    app.run(debug=False)