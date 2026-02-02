#!/bin/bash

fuser -k 8080/tcp &
fuser -k 8761/tcp &
fuser -k 17311/tcp &
fuser -k 17312/tcp &
fuser -k 17313/tcp