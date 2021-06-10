import tensorflow as tf


class AudioDataProcessing:
    def __init__(self):
        self.pad_len = 2754

    def db_scale(self, log_spectrogram, amin=1e-16, top_db=80.0):
        """Convert spectrogram to decibel units"""

        def _tf_log10(x):
            numerator = tf.math.log(x)
            denominator = tf.math.log(tf.constant(10, dtype=numerator.dtype))
            return numerator / denominator

        ref = tf.reduce_max(log_spectrogram)

        log_spec = 10.0 * _tf_log10(tf.maximum(amin, log_spectrogram))
        log_spec -= 10.0 * _tf_log10(tf.maximum(amin, ref))

        log_spec = tf.maximum(log_spec, tf.reduce_max(log_spec) - top_db)

        return log_spec

    def get_spectrogram(self, waveform):
        """Create spectrogram from audio wave form"""
        # Extracting log spectrogram from audio waveform
        waveform = tf.cast(waveform, tf.float32)
        spectrogram = tf.signal.stft(
            waveform,
            frame_length=200,
            frame_step=80,
            fft_length=256)
        log_spectrogram = tf.math.pow(tf.abs(spectrogram), 0.5)
        log_spectrogram = self.db_scale(log_spectrogram)

        # normalisation
        means = tf.math.reduce_mean(log_spectrogram, 1, keepdims=True)
        stddev = tf.math.reduce_std(log_spectrogram, 1, keepdims=True)
        x = (log_spectrogram - means) / stddev

        # padding to 20 seconds
        pad_len = self.pad_len
        paddings = tf.constant([[0, pad_len], [0, 0]])
        x = tf.pad(x, paddings, "CONSTANT")[:pad_len, :]
        x = tf.where(tf.math.is_nan(x), tf.ones_like(x) * 0, x)
        return x
