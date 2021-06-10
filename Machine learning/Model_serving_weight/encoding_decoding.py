import tensorflow as tf
import numpy as np
import tensorflow_io as tfio

from string import ascii_lowercase


class EncodingDecoding:
    def __init__(self):
        self.char = (["-", "#", "<", ">"]
                     + [c for c in ascii_lowercase]
                     + [" ", ".", ",", "?", "'"])

    def encode_label(self, label):
        keys_tensor = tf.constant(self.char)
        vals_tensor = tf.constant(np.arange(len(self.char)))
        input_tensor = label

        table = tf.lookup.StaticHashTable(
            tf.lookup.KeyValueTensorInitializer(keys_tensor, vals_tensor),
            default_value=30)

        return table.lookup(input_tensor)

    def decode_label(self, predicted_label):
        keys_tensor = tf.constant(np.arange(len(self.char)))
        vals_tensor = tf.constant(self.char)
        input_tensor = tf.cast(predicted_label, dtype=tf.int32)

        table = tf.lookup.StaticHashTable(
            tf.lookup.KeyValueTensorInitializer(keys_tensor, vals_tensor),
            default_value='')

        return table.lookup(input_tensor)

    @staticmethod
    def decode_audio(audio_file):
        """ decode audio file to float tensor"""
        audio = tfio.IOTensor.graph(tf.int16).from_audio(audio_file)
        audio_tensor = tf.squeeze(audio.to_tensor(), axis=-1)
        waveform = tf.cast(audio_tensor, tf.float32) / 32768.0

        return waveform
