import tensorflow as tf

from audio_label_decoding import Decode
from audio_processing import AudioDataProcessing

MODEL_PATH = 'speech_to_text'


class _STT_Service:
    model = None
    _instance = None

    def preprocess(self, file_path):
        # Convert audio_file to waveform
        waveform = Decode().decode_audio(file_path)

        # Extract db-scale spectrogram
        spectrogram = AudioDataProcessing().get_spectrogram(waveform)
        return spectrogram

    def decode_prediction(self, pred_result):
        dec_input = tf.constant(30, tf.int32)[tf.newaxis]
        label = tf.squeeze(pred_result, axis=0)

        # Exclude sets after element value of 3
        for i in label[1:]:
            if tf.math.logical_not(tf.math.equal(i, 3)):
                i = tf.expand_dims(i, axis=0)
                dec_input = tf.concat([dec_input, i], axis=0)
            else:
                break

        #  Decode prediction result from numeric to alphabetic
        label = Decode().decode_label(tf.cast(dec_input, dtype=tf.int64))

        label = b''.join(label.numpy()).decode('utf-8')
        return label

    def predict(self, file_path):
        # Extract db-scale spectrogram
        spectrogram = self.preprocess(file_path)

        # Expand spectrogram dimension for batch channel
        spectrogram = tf.expand_dims(spectrogram, axis=0)

        # Make inference
        predict = self.model.generate(spectrogram)

        # Decode value
        predict = self.decode_prediction(predict)

        return predict.strip()


def STT_Service():
    if _STT_Service._instance is None:
        _STT_Service._instance = _STT_Service()
        _STT_Service.model = tf.saved_model.load(MODEL_PATH)
    return _STT_Service._instance
