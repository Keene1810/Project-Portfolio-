using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace test_ngo.Models
{
    public class Testimonials
    {
        [Key]
        public int TestimonialID { get; set; }
        public string Author { get; set; }
        public string Description { get; set; }
        public DateOnly Date { get; set; }
        public string Position { get; set; }
        
        [NotMapped]
        [Display(Name = "Image")]
        public IFormFile ImageFile { get; set; }

        public byte[]? ImageData { get; set; }
    }
}
