from mir.fma import utils


tracks = utils.load("data/fma_metadata/tracks.csv")


print(tracks)

echonest = utils.load('data/fma_metadata/echonest.csv')

print(echonest)