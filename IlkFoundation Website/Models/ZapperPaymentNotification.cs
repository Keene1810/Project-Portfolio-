namespace test_ngo.Models
{
    public class ZapperPaymentNotification
    {
        public string Signature { get; set; }
        public string SessionId { get; set; }
        public decimal Amount { get; set; }
        public string CurrencyISOCode { get; set; }
        public string MerchantOrderId { get; set; }
        public string PaymentReference { get; set; }
        public string PaymentStatus { get; set; }
        public string PaymentUTCDate { get; set; }
        public string UserMessage { get; set; }
    }
}
