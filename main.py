from flask import Flask, request
import os, json

import numpy as np
from predict import predict as prediction

app = Flask(__name__)

@app.route('/', methods=["GET"])
def index():
    data = {"status": 200, "data": "Hello world"}
    umur = {
        "budi": 20,
        "rani": 21,
        "rara": 23,
        "ruru": 19
    }
    data["val_umur"] = umur
    return json.dumps(data)

@app.route('/input/<int:id>', methods=['GET'])
def input_id(id):
    parameter = str(id)
    return parameter

@app.route('/input/post/', methods=['POST'])
def input_post():
    args1 = request.args.get("kalimat") #diterima di sini
    input_predict = np.array([args1])

    response = prediction(input_predict.tolist())
    return response


@app.route('/api/predict/test', methods=['GET'])
def predict_test():
    words = 'bangun tidur siang tengah aneh pindah texas milik konsentrasi halhal kerja rumah kelas 10 cepat jam ' \
            'dentang 4 henti mudah laku pindah kerja rumah tantang kerja sibuk putus habis berjamjam laku bayar ' \
            'perhati kelas barangbarang benarbenar keras tinggal lacak tahun malas jenius hei lambat baik benarbenar ' \
            'fokus tinggal kampus konsentrasi mudah sayang tinggal rumah awas ketat tua omel adik omel omel omel ' \
            'titik repot pergi jalan sekolah pergi pustaka ajar pindah memberitahu salah pindah pergi lindung milik ' \
            'khawatir dunia satusatunya jaga kamar bersih bantu bisnis uang ut hidup asrama apartemen semester pikir ' \
            'ambil untung off topik pergi jalan enam malam milik ledak cinta austin tinggal va pergi dc waktu milik ' \
            'ledak siswa lari malam bersenangsenang tanggung bersenangsenang prioritas lurus tinggal rumah kau harap ' \
            'laku tanggung adik kacau pergi gila pindah guru tinggi kacau karir guru tinggi pesta alas utama pergi ' \
            'bersenangsenang biar pergi jajah dunia india budaya india nilainilai india lawan bersenangsenang maksud ' \
            'temu orangorang pacar pesta bersenangsenang sekolah sulit pikir milik bebas tempat tekan buat sekolah ' \
            'tua harap senang tulis pergi tulis bantu pikir urut harap bersenangsenang baca untung ta '
    # words = request.args.get("nama")
    input = np.array([words])

    test_data = {
        'instances': input.tolist()
    }
    data = json.dumps(test_data)
    response_json = prediction(input.tolist())

    return response_json

if __name__ == '__main__':
    # app.run()
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))