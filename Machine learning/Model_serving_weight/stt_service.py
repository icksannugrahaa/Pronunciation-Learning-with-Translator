import tensorflow as tf

from tensorflow import keras
from model import Transformer
from audio_label_processing import AudioDataProcessing, LabelProcessing
from encoding_decoding import EncodingDecoding

WEIGHT_PATH = 'weights/my_weights'


class _STT_Service:
    model = None
    _instance = None

    def preprocess(self, file_path):
        # Convert audio_file to waveform
        waveform = EncodingDecoding().decode_audio(file_path)

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
        label = EncodingDecoding().decode_label(tf.cast(dec_input, dtype=tf.int64))

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


def create_model(optimizers):
    model = Transformer(
        num_hid=256,
        num_head=4,
        num_feed_forward=1024,
        target_maxlen=200,
        num_layers_enc=4,
        num_layers_dec=1,
        num_classes=35,
    )
    loss_fn = tf.keras.losses.CategoricalCrossentropy(
        from_logits=True, label_smoothing=0.1,
    )
    model.compile(optimizer=optimizers, loss=loss_fn)

    return model


def STT_Service():
    if _STT_Service._instance is None:
        optimizer = keras.optimizers.Adam(beta_1=0.9, beta_2=0.98,
                                          epsilon=1e-9)
        model = create_model(optimizer)

        decoding = EncodingDecoding()
        audio_processing = AudioDataProcessing()
        label_processing = LabelProcessing()

        # Convert audio file to db-scale spectrogram
        waveform = decoding.decode_audio('audio_initialize.wav')
        spectrogram = audio_processing.get_spectrogram(waveform)
        x = tf.expand_dims(spectrogram, axis=0)

        # Encode label
        label = label_processing.get_label('text_initialize.txt')
        y = tf.expand_dims(label, axis=0)

        model.train_on_batch(x, y)
        model.load_weights("weights/my_weights")

        _STT_Service._instance = _STT_Service()
        _STT_Service.model = model
    return _STT_Service._instance
