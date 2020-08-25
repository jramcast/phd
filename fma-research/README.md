# Research with FMA dataset

## Requirements

* Python 3
* Conda


## Getting started


Create conda environment and install dependencies:

### Linux 64 bits

```sh
conda env create -f env.yml
```

### Other platforms

```sh
conda env create -f env-multiplatform.yml
```


Then, to activate the environment, use:

```sh
conda activate fma
```

## Development

To export the conda environment (coupled to your current platform):

```sh
conda env export > env.yml
```

To export the conda environment for multi-platform:

```sh
conda env export --from-history > env-multiplatform.yml
```