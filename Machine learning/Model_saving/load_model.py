import tensorflow as tf

from encoding_decoding import EncodingDecoding
from audio_label_processing import AudioDataProcessing
from tensorflow.keras import models


def inference(model, spectrogram):
    dec_input = tf.constant(30, tf.int32)[tf.newaxis]
    label = model.generate(spectrogram)
    label = tf.squeeze(label, axis=0)

    for i in label[1:]:
        if tf.math.logical_not(tf.math.equal(i, 3)):
            i = tf.expand_dims(i, axis=0)
            dec_input = tf.concat([dec_input, i], axis=0)
        else:
            break

    decoding = EncodingDecoding()
    label = decoding.decode_label(tf.cast(dec_input, dtype=tf.int64))

    label = b''.join(label.numpy()).decode('utf-8')
    return label.strip()


if __name__ == "__main__":
    decoding = EncodingDecoding()
    audio_processing = AudioDataProcessing()

    # Convert audio file to db-scale spectrogram
    waveform = decoding.decode_audio('audio_initialize.wav')
    spectrogram = audio_processing.get_spectrogram(waveform)
    x = tf.expand_dims(spectrogram, axis=0)

    model = models.load_model('speech_to_text')

    test = inference(model, x)
    print(test)
