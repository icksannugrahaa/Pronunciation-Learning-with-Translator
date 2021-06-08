import os
import tarfile
import tensorflow as tf
from tensorflow import keras
from model import Transformer
from encoding_decoding import EncodingDecoding
from audio_label_processing import AudioDataProcessing, LabelProcessing


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


if __name__ == '__main__':
    archive_path = 'weights.tar.gz'
    if not os.path.exists(archive_path):
        raise Exception(f'there are no {archive_path}')
    elif not os.path.exists('content/weights/my_weights'):
        my_tar = tarfile.open(archive_path)
        my_tar.extractall('./')
        my_tar.close()

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
    model.load_weights("content/weights/my_weights")

    model.save('speech_to_text')
