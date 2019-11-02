using System;
using System.Runtime.InteropServices;

namespace Bionic
{
    class LibScanApi
    {
        [DllImport("ftrScanAPI.dll")]
        internal static extern IntPtr ftrScanOpenDevice();

        [DllImport("ftrScanAPI.dll")]
        internal static extern long GetLastError();

        [DllImport("ftrScanAPI.dll")]
        internal static extern void ftrScanCloseDevice(IntPtr ftrHandle);

        [DllImport("ftrScanAPI.dll")]
        internal static extern bool ftrScanIsFingerPresent(IntPtr ftrHandle, out _FTRSCAN_FRAME_PARAMETERS pFrameParameters);

        [DllImport("ftrScanAPI.dll")]
        internal static extern bool ftrScanSetDiodesStatus(IntPtr ftrHandle, byte byGreenDiodeStatus, byte byRedDiodeStatus);

        [DllImport("ftrScanAPI.dll")]
        internal static extern bool ftrScanGetDiodesStatus(IntPtr ftrHandle, out bool pbIsGreenDiodeOn, out bool pbIsRedDiodeOn);

        [DllImport("ftrScanAPI.dll")]
        internal static extern bool ftrScanGetImageSize(IntPtr ftrHandle, out _FTRSCAN_IMAGE_SIZE pImageSize);

        [DllImport("ftrScanAPI.dll")]
        internal static extern bool ftrScanGetImage(IntPtr ftrHandle, int nDose, byte[] pBuffer);


        internal struct _FTRSCAN_FAKE_REPLICA_PARAMETERS
        {
            public bool bCalculated;
            public int nCalculatedSum1;
            public int nCalculatedSumFuzzy;
            public int nCalculatedSumEmpty;
            public int nCalculatedSum2;
            public double dblCalculatedTremor;
            public double dblCalculatedValue;
        }

        internal struct _FTRSCAN_FRAME_PARAMETERS
        {
            public int nContrastOnDose2;
            public int nContrastOnDose4;
            public int nDose;
            public int nBrightnessOnDose1;
            public int nBrightnessOnDose2;
            public int nBrightnessOnDose3;
            public int nBrightnessOnDose4;
            public _FTRSCAN_FAKE_REPLICA_PARAMETERS FakeReplicaParams;
            public _FTRSCAN_FAKE_REPLICA_PARAMETERS Reserved;
        }

        internal struct _FTRSCAN_IMAGE_SIZE
        {
            public int nWidth;
            public int nHeight;
            public int nImageSize;
        }

    }
}