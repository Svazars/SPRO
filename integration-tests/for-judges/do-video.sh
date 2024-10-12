
export FRAMERATE=30

cd ./tmp/.vis-replay
ffmpeg -r $FRAMERATE -i replay_%05d.png -c:v libx264 -pix_fmt yuv420p out.mp4
cp out.mp4 ../..
