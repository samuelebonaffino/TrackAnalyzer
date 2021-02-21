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
void setup()
{
    frameRate(60);
    initSystem("butterflies.wav");
}
void draw()
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
String getDate()
{
    return ( month()  + "-" + day() + "_" 
           + hour()   + "h" 
           + minute() + "m"
           + second() + "s");
}
void initSystem(String sample)
{
    spectrum  = new float[F][BANDS];
    amps      = new float[F];
    outputFFT = createWriter("analysis/fft_" + getDate() + ".txt");
    outputAMP = createWriter("analysis/amp_" + getDate() + ".txt");
    audio = new Audio(BANDS, sample);
    audio.play();
}
void copySpectrumToArray()
{
    audio.updateSpectrum();  
    for(int i = 0; i < BANDS; i++)
        spectrum[frames][i] = audio.getFrequency(i);
}
void writeSpectrum()
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
void writeAmplitudes()
{
    for(float a : amps)
        outputAMP.println(a);
    outputAMP.flush();
    outputAMP.close();
}
void printState()
{
    if(frameCount%60 == 0)
    {
        float perc = frames * (100.0/(float)F);
        println("Loading... " + round(perc) + "%");
    }
}

// Debug methods
void DEBUG_printSpectrum()
{
    char[] s = new char[BANDS];

    for(int i = 0; i < F; i++)
        println("ROW#" + i + ": " 
                + spectrum[i][0] + " " +
                + spectrum[i][1] + " " +
                + spectrum[i][2] + " ");
}