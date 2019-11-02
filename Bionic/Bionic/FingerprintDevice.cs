using System;
using System.Drawing;
using System.Threading;

namespace Bionic
{
    public class FingerprintDevice : IDisposable
    {
        private const int FingerPresenseCheckIntervalInMs = 500;
        private const int FingerDetectionContrastThreshold = 800;
        private const int NDose = 4;

        private readonly IntPtr handle;
        private readonly Timer fingerDetectionTimer;

        private readonly object ledStatusWritingLock = new object();
        private LibScanApi._FTRSCAN_FRAME_PARAMETERS pFrameParameters;
        private bool greenIsOn;
        private bool redIsOn;

        public event EventHandler FingerDetected;
        public event EventHandler FingerReleased;

        public FingerprintDevice(IntPtr handle)
        {
            this.handle = handle;
            this.fingerDetectionTimer = new Timer(this.FingerDetectionCallback, null, Timeout.Infinite, Timeout.Infinite);
        }

        public bool GreenLed
        {
            get { return this.GetLedState().GreenIsOn;}
            set {  this.SetGreenLed(value);}
        }

        public bool RedLed
        {
            get { return this.GetLedState().RedIsOn;}
            set {  this.SetRedLed(value);}
        }

        public bool IsFingerPresent { get; private set; }

        public void StartFingerDetection()
        {
            fingerDetectionTimer.Change(0, FingerPresenseCheckIntervalInMs);
        }
        public void StopFingerDetection()
        {
            fingerDetectionTimer.Change(Timeout.Infinite, Timeout.Infinite);
        }

        private void FingerDetectionCallback(object state)
        {
            var result = LibScanApi.ftrScanIsFingerPresent(this.handle, out pFrameParameters);

            if (!result)
            {
                // There could be an error
                // var error = LibScanApi.GetLastError();
                return;
            }

            var lastFingerDetectedResult = pFrameParameters.nContrastOnDose2 > FingerDetectionContrastThreshold;

            if (lastFingerDetectedResult && !this.IsFingerPresent)
            {
                this.IsFingerPresent = true;
                this.OnFingerDetected();
            }

            if (!lastFingerDetectedResult && this.IsFingerPresent)
            {
                this.IsFingerPresent = false;
                this.OnFingerReleased();
            }
        }

        public void Dispose()
        {
            this.fingerDetectionTimer.Change(Timeout.Infinite, Timeout.Infinite);
            this.fingerDetectionTimer.Dispose();

            LibScanApi.ftrScanCloseDevice(this.handle);
        }

        protected virtual void OnFingerDetected()
        {
            FingerDetected?.Invoke(this, EventArgs.Empty);
        }

        public LedState GetLedState()
        {
            LibScanApi.ftrScanGetDiodesStatus(this.handle, out greenIsOn, out redIsOn);

            return new LedState()
            {
                GreenIsOn = greenIsOn,
                RedIsOn = redIsOn,
            };
        }

        public void SwitchLedState(bool green, bool red)
        {
            LibScanApi.ftrScanSetDiodesStatus(this.handle, (byte)(green ? 255 : 0), (byte)(red ? 255 : 0));
        }

        public Bitmap ReadFingerprint()
        {
            var t = new LibScanApi._FTRSCAN_IMAGE_SIZE();
            LibScanApi.ftrScanGetImageSize(this.handle, out t);

            byte[] arr = new byte[t.nImageSize];
            LibScanApi.ftrScanGetImage(this.handle, NDose, arr);

            var width = t.nWidth;
            var height = t.nHeight;

            var image = new Bitmap(width, height, System.Drawing.Imaging.PixelFormat.Format24bppRgb);

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    byte a = (byte)(0xFF - arr[(y * width) + x]);

                    image.SetPixel(x, y, Color.FromArgb(a, a, a));
                }
            }

            return image;
        }

        protected void SetGreenLed(bool state)
        {
            lock (this.ledStatusWritingLock)
            {
                LibScanApi.ftrScanGetDiodesStatus(this.handle, out greenIsOn, out redIsOn);

                greenIsOn = state;

                LibScanApi.ftrScanSetDiodesStatus(this.handle, (byte)(greenIsOn ? 255 : 0), (byte)(redIsOn ? 255 : 0));
            }
        }

        protected void SetRedLed(bool state)
        {
            lock (this.ledStatusWritingLock)
            {
                LibScanApi.ftrScanGetDiodesStatus(this.handle, out greenIsOn, out redIsOn);

                redIsOn = state;

                LibScanApi.ftrScanSetDiodesStatus(this.handle, (byte)(greenIsOn ? 255 : 0), (byte)(redIsOn ? 255 : 0));
            }
        }

        protected virtual void OnFingerReleased()
        {
            FingerReleased?.Invoke(this, EventArgs.Empty);
        }
    }

    public class LedState
    {
        public bool GreenIsOn { get; set; }

        public bool RedIsOn { get; set; }
    }
}
