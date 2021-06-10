from stt_service import STT_Service

FILE_PATH = "audio_initialize.wav"

if __name__ == '__main__':
    stt = STT_Service()
    stt: str = stt.predict(FILE_PATH)
    print(stt)


