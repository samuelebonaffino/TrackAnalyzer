import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

// FFT Analyzer
final static int F = 3600;
final static int BANDS = 64;

int frames = 0;
float[][] spectrum;
float[] amps;
PrintWriter outputFFT;
PrintWriter outputAMP;
Audio audio;

// Processing methods
public void setup()
{
    frameRate(60);
    initSystem("butterflies.wav");
}
public void draw()
{
    printState();
    if(frames < F)
    {
        copySpectrumToArray();
        amps[frames] = audio.getAmplitude();
        frames++;
    }
    else 
    {
        writeSpectrum();
        writeAmplitudes();
        exit();
    }
}

// My methods
public String getDate()
{
    return ( month()  + "-" + day() + "_" 
           + hour()   + "h" 
           + minute() + "m"
           + second() + "s");
}
public void initSystem(String sample)
{
    spectrum  = new float[F][BANDS];
    amps      = new float[F];
    outputFFT = createWriter("analysis/fft_" + getDate() + ".txt");
    outputAMP = createWriter("analysis/amp_" + getDate() + ".txt");
    audio = new Audio(BANDS, sample);
    audio.play();
}
public void copySpectrumToArray()
{
    audio.updateSpectrum();  
    for(int i = 0; i < BANDS; i++)
        spectrum[frames][i] = audio.getFrequency(i);
}
public void writeSpectrum()
{
    StringBuilder sb = new StringBuilder();

    for(int i = 0; i < F; i++)
    {
        for(int j = 0; j < BANDS; j++)
            sb.append(spectrum[i][j] + " ");
        outputFFT.println(sb.toString());
        sb.setLength(0);
    }
    outputFFT.flush();
    outputFFT.close();
}
public void writeAmplitudes()
{
    for(float a : amps)
        outputAMP.println(a);
    outputAMP.flush();
    outputAMP.close();
}
public void printState()
{
    if(frameCount%60 == 0)
    {
        float perc = frames * (100.0f/(float)F);
        println("Loading... " + round(perc) + "%");
    }
}

// Debug methods
public void DEBUG_printSpectrum()
{
    char[] s = new char[BANDS];

    for(int i = 0; i < F; i++)
        println("ROW#" + i + ": " 
                + spectrum[i][0] + " " +
                + spectrum[i][1] + " " +
                + spectrum[i][2] + " ");
}


class Audio
{
    // Attributes
    String name;
    int band;
    float smoothingFactor = 0.5f;
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
    public void cue(float time)
    {
        input.cue(time);
    }
    public void play()
    {
        input.play();
    }
    public void updateSpectrum()
    {
        fft.analyze();
    }
    public int getSpectrumID(int index)
    {
        return index % band;
    }
    public float getFrequency(int id)
    {
        if(id >= band)
            id = getSpectrumID(id);
        for(int i = 0; i <= id; i++)
            sum[i] += (fft.spectrum[i] - sum[i]) * smoothingFactor;
        return sum[id];
    }
    public float getAmplitude()
    {
        return amp.analyze();
    }
    public float getAmplitude(float mult)
    {
        return amp.analyze() * mult;
    }
    public boolean isPlaying()
    {
        return input.isPlaying();
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
