using System;

namespace Bionic
{
    public class DeviceAccessor
    {
        public FingerprintDevice AccessFingerprintDevice()
        {
            var handle = LibScanApi.ftrScanOpenDevice();

            if (handle != IntPtr.Zero)
            {
                return new FingerprintDevice(handle);
            }

            throw new Exception("Cannot open device");
        }

        public CardReader AccessCardReader()
        {
            var handle = LibMifareApi.ftrMFOpenDevice();

            if (handle != IntPtr.Zero)
            {
                return new CardReader(handle);
            }

            throw new Exception("Cannot open device");
        }
    }
}