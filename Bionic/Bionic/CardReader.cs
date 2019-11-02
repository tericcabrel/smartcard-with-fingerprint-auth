using System;
using System.Threading;

namespace Bionic
{
    public class CardReader : IDisposable
    {
        private const int CardPresenseCheckIntervalInMs = 50;

        private IntPtr handle;
        private Timer cardDetectionTimer;

        public event EventHandler<CardDetectedEventArgs> CardDetected;
        public event EventHandler<EventArgs> CardRemoved;

        public CardReader(IntPtr handle)
        {
            this.handle = handle;

            this.cardDetectionTimer = new Timer(this.CardDetectionCallback, null, Timeout.Infinite, Timeout.Infinite);
        }

        public void StartCardDetection()
        {
            this.cardDetectionTimer.Change(CardPresenseCheckIntervalInMs, CardPresenseCheckIntervalInMs);
        }

        public bool IsCardPresent { get; private set; }

        public ulong CardSerialNumber { get; private set; }

        private void CardDetectionCallback(object state)
        {
            LibMifareApi.ftrMFStartSequence(handle);

            var cardTypeBytes = new byte[1];
            var serialNumberBytes = new byte[8];

            LibMifareApi.ftrMFActivateIdle8bSN(this.handle, cardTypeBytes, serialNumberBytes);

            LibMifareApi.ftrMFEndSequence(this.handle);

            var cardType = (CardType)cardTypeBytes[0];

            var isCardPresentNow = cardType != CardType.Invalid;

            if (isCardPresentNow != IsCardPresent)
            {
                if (isCardPresentNow)
                {
                    var serialNumber = BitConverter.ToUInt64(serialNumberBytes, 0);

                    this.CardSerialNumber = serialNumber;
                    this.CardType = cardType;
                    this.IsCardPresent = true;

                    this.OnCardDetected(new CardDetectedEventArgs { SerialNumber = serialNumber, Type = cardType });
                }
                else
                {
                    this.IsCardPresent = false;
                    this.CardType = CardType.Invalid;
                    this.CardSerialNumber = 0;

                    this.OnCardRemoved();
                }
            }

        }

        public CardType CardType { get; private set; }

        public void Dispose()
        {
            this.cardDetectionTimer.Change(Timeout.Infinite, Timeout.Infinite);

            this.cardDetectionTimer.Dispose();

            LibMifareApi.ftrMFCloseDevice(this.handle);
        }

        protected virtual void OnCardDetected(CardDetectedEventArgs e)
        {
            CardDetected.Invoke(this, e);
        }

        protected virtual void OnCardRemoved()
        {
            CardRemoved.Invoke(this, EventArgs.Empty);
        }
    }

    public class CardDetectedEventArgs : EventArgs
    {
        public ulong SerialNumber { get; set; }
        public CardType Type { get; set; }
    }
}