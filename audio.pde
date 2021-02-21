import processing.sound.*;

class Audio
{
    // Attributes
    String name;
    int band;
    float smoothingFactor = 0.5;
    SoundFile input;
    float[] sum;
    FFT fft;
    Amplitude amp;

    // Constructor
    Audio(int band, String name)
    {
        this.band = band;
        this.name = name;

        input = new SoundFile(main.this, name);
        sum = new float[band];
        fft = new FFT(main.this, band);
        amp = new Amplitude(main.this);

        fft.input(input);
        amp.input(input);
    }

    // Methods
    void cue(float time)
    {
        input.cue(time);
    }
    void play()
    {
        input.play();
    }
    void updateSpectrum()
    {
        fft.analyze();
    }
    int getSpectrumID(int index)
    {
        return index % band;
    }
    float getFrequency(int id)
    {
        if(id >= band)
            id = getSpectrumID(id);
        for(int i = 0; i <= id; i++)
            sum[i] += (fft.spectrum[i] - sum[i]) * smoothingFactor;
        return sum[id];
    }
    float getAmplitude()
    {
        return amp.analyze();
    }
    float getAmplitude(float mult)
    {
        return amp.analyze() * mult;
    }
    boolean isPlaying()
    {
        return input.isPlaying();
    }
}