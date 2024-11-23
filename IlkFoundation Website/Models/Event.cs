using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace test_ngo.Models
{
    public class Event
    {
        [Key]
        public int EventID { get; set; }
        public string EventName { get; set; }
        public DateTime EventDate { get; set; }
        public string EventLocation { get; set; }
        public string Description { get; set; }
        [NotMapped]
        [Display(Name = "Image")]
        public IFormFile ImageFile { get; set; }

        public byte[]? ImageData { get; set; }
    }
}
