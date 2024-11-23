using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using test_ngo.Data;
using test_ngo.Models;
using static System.Reflection.Metadata.BlobBuilder;

namespace test_ngo.Controllers
{
    
    public class TestimonialsController : Controller
    {
        private readonly ApplicationDbContext _context;

        public TestimonialsController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: Testimonials
        public async Task<IActionResult> Index()
        {
            return View(await _context.Testimonials.ToListAsync());
        }

        // GET: Testimonials/Details/5
        public async Task<IActionResult> Details(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var testimonials = await _context.Testimonials
                .FirstOrDefaultAsync(m => m.TestimonialID == id);
            if (testimonials == null)
            {
                return NotFound();
            }

            return View(testimonials);
        }

        // GET: Testimonials/Create
        public IActionResult Create()
        {
            return View();
        }

        // POST: Testimonials/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(Testimonials testimonials, IFormFile imageFile)
        {
            // Check if an image file is provided
            if (imageFile != null && imageFile.Length > 0)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await imageFile.CopyToAsync(memoryStream);
                    testimonials.ImageData = memoryStream.ToArray();
                }
            }
            if (ModelState.IsValid)
            {
                _context.Add(testimonials);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            return View(testimonials);
        }

        // GET: Testimonials/Edit/5
        public async Task<IActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var testimonials = await _context.Testimonials.FindAsync(id);
            if (testimonials == null)
            {
                return NotFound();
            }
            return View(testimonials);
        }

        // POST: Testimonials/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, Testimonials testimonials, IFormFile imageFile)
        {
            if (id != testimonials.TestimonialID)
            {
                return NotFound();
            }

            if (ModelState.IsValid)
            {
                try
                {
                    // Check if an image file is provided
                    if (imageFile != null && imageFile.Length > 0)
                    {
                        using (var memoryStream = new MemoryStream())
                        {
                            await imageFile.CopyToAsync(memoryStream);
                            testimonials.ImageData = memoryStream.ToArray();
                        }
                    }
                    _context.Update(testimonials);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!TestimonialsExists(testimonials.TestimonialID))
                    {
                        return NotFound();
                    }
                    else
                    {
                        throw;
                    }
                }
                return RedirectToAction(nameof(Index));
            }
            return View(testimonials);
        }

        // GET: Testimonials/Delete/5
        public async Task<IActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var testimonials = await _context.Testimonials
                .FirstOrDefaultAsync(m => m.TestimonialID == id);
            if (testimonials == null)
            {
                return NotFound();
            }

            return View(testimonials);
        }

        // POST: Testimonials/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            var testimonials = await _context.Testimonials.FindAsync(id);
            if (testimonials != null)
            {
                _context.Testimonials.Remove(testimonials);
            }

            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool TestimonialsExists(int id)
        {
            return _context.Testimonials.Any(e => e.TestimonialID == id);
        }
    }
}
