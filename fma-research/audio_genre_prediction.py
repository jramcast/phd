import os
import pandas as pd
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.layers import Activation, Dense, Conv1D, Conv2D, MaxPooling1D, Flatten, Reshape
from sklearn.preprocessing import LabelBinarizer
from mir.fma import utils


try:
    AUDIO_DIR = os.environ["AUDIO_DIR"]
except Exception:
    print("AUDIO_DIR not define. Define this variable")
    exit(1)


tracks = utils.load("data/fma_metadata/tracks.csv")
echonest = utils.load('data/fma_metadata/echonest.csv')

# Check all echonest features rows are linked to existing tracks
assert echonest.index.isin(tracks.index).all()

# Use small FMA
subset = tracks.index[tracks['set', 'subset'] <= 'small']
tracks = tracks.loc[subset]

# Remove corrupted files
# tracks = tracks.drop([98565, 98567, 98569, 99134, 108925, 133297], axis=0)
# print("Tracks: ", tracks.shape)

train = tracks.index[tracks['set', 'split'] == 'training']
val = tracks.index[tracks['set', 'split'] == 'validation']
test = tracks.index[tracks['set', 'split'] == 'test']

print('{} training examples, {} validation examples, {} testing examples'.format(*map(len, [train, val, test])))

# Binarize genres
labels_onehot = LabelBinarizer().fit_transform(tracks['track', 'genre_top'])
labels_onehot = pd.DataFrame(labels_onehot, index=tracks.index)

# Check audio paths are correct by loading sample #2
utils.FfmpegLoader().load(utils.get_audio_path(AUDIO_DIR, 2))

# Check it can iterate through training ids
SampleLoader = utils.build_sample_loader(
    AUDIO_DIR,
    labels_onehot,
    utils.FfmpegLoader())
SampleLoader(train, batch_size=2).__next__()[0].shape

# Keras parameters.
NB_WORKER = len(os.sched_getaffinity(0))  # number of usable CPUs
params = {'use_multiprocessing': True, 'workers': NB_WORKER, 'max_queue_size': 10}


# Define network

loader = utils.FfmpegLoader(sampling_rate=2000)
SampleLoader = utils.build_sample_loader(AUDIO_DIR, labels_onehot, loader)
print('Dimensionality: {}'.format(loader.shape))

keras.backend.clear_session()

units = 10

model = keras.models.Sequential()
model.add(Dense(10, input_shape=loader.shape))
model.add(Activation("relu"))
model.add(Dense(5))
model.add(Activation("relu"))
model.add(Dense(labels_onehot.shape[1]))
model.add(Activation("softmax"))

optimizer = keras.optimizers.SGD(lr=0.1, momentum=0.9, nesterov=True)
model.compile(optimizer, loss='categorical_crossentropy', metrics=['accuracy'])

model.fit_generator(SampleLoader(train, batch_size=64), train.size, epochs=2, **params)
loss = model.evaluate_generator(SampleLoader(val, batch_size=64), val.size, **params)
loss = model.evaluate_generator(SampleLoader(test, batch_size=64), test.size, **params)
#Y = model.predict_generator(SampleLoader(test, batch_size=64), test.size, **params);

print(loss)