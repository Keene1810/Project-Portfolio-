using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace test_ngo.Models
{
    public class PastEvents
    {
        [Key]
        public int EventId { get; set; }

        [Required]
        [StringLength(100)]
        public string Title { get; set; }

        [Required]
        [StringLength(500)]
        public string Description { get; set; }

        [Required]
        public DateTime EventDate { get; set; }

        [NotMapped]
        [Display(Name = "Image")]
        public IFormFile ImageFile { get; set; }

        public byte[]? ImageData { get; set; }
    }
}
