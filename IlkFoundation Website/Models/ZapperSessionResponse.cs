namespace test_ngo.Models
{

    public class ZapperSessionResponse
    {
        public string? SessionId { get; set; }
        public string? RedirectUrl { get; set; }
        public string? Status { get; set; }
        public string[]? Errors { get; set; }
    }
}
