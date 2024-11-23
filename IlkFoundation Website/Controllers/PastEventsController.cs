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
    
    public class PastEventsController : Controller
    {
        private readonly ApplicationDbContext _context;

        public PastEventsController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: PastEvents
        public async Task<IActionResult> Index()
        {
            return View(await _context.PastEvents.ToListAsync());
        }

        // GET: PastEvents/Details/5
        public async Task<IActionResult> Details(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var pastEvents = await _context.PastEvents
                .FirstOrDefaultAsync(m => m.EventId == id);
            if (pastEvents == null)
            {
                return NotFound();
            }

            return View(pastEvents);
        }

        // GET: PastEvents/Create
        public IActionResult Create()
        {
            return View();
        }

        // POST: PastEvents/Create
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create( PastEvents pastEvents, IFormFile imageFile)
        {
            // Check if an image file is provided
            if (imageFile != null && imageFile.Length > 0)
            {
                using (var memoryStream = new MemoryStream())
                {
                    await imageFile.CopyToAsync(memoryStream);
                    pastEvents.ImageData = memoryStream.ToArray();
                }
            }
            if (ModelState.IsValid)
            {
                _context.Add(pastEvents);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            return View(pastEvents);
        }

        // GET: PastEvents/Edit/5
        public async Task<IActionResult> Edit(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var pastEvents = await _context.PastEvents.FindAsync(id);
            if (pastEvents == null)
            {
                return NotFound();
            }
            return View(pastEvents);
        }

        // POST: PastEvents/Edit/5
        // To protect from overposting attacks, enable the specific properties you want to bind to.
        // For more details, see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, PastEvents pastEvents, IFormFile imageFile)
        {
            if (id != pastEvents.EventId)
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
                            pastEvents.ImageData = memoryStream.ToArray();
                        }
                    }
                    _context.Update(pastEvents);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!PastEventsExists(pastEvents.EventId))
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
            return View(pastEvents);
        }

        // GET: PastEvents/Delete/5
        public async Task<IActionResult> Delete(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var pastEvents = await _context.PastEvents
                .FirstOrDefaultAsync(m => m.EventId == id);
            if (pastEvents == null)
            {
                return NotFound();
            }

            return View(pastEvents);
        }

        // POST: PastEvents/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            var pastEvents = await _context.PastEvents.FindAsync(id);
            if (pastEvents != null)
            {
                _context.PastEvents.Remove(pastEvents);
            }

            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        // Action to display past events
        public async Task<IActionResult> DisplayPastEvents()
        {
            var events = await _context.PastEvents.ToListAsync();
            return View(events);
        }

        private bool PastEventsExists(int id)
        {
            return _context.PastEvents.Any(e => e.EventId == id);
        }
    }
}
